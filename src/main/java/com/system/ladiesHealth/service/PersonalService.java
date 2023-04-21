package com.system.ladiesHealth.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.system.ladiesHealth.dao.*;
import com.system.ladiesHealth.domain.dto.BasicSignDTO;
import com.system.ladiesHealth.domain.dto.MenstrualDTO;
import com.system.ladiesHealth.domain.po.*;
import com.system.ladiesHealth.domain.vo.*;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import com.system.ladiesHealth.utils.RollbackUtil;
import com.system.ladiesHealth.utils.convert.PersonalConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonalService {

    @Autowired
    private SignInfoRepository signInfoRepository;

    @Autowired
    private SleepRecordRepository sleepRecordRepository;

    @Autowired
    private DrinkRecordRepository drinkRecordRepository;

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private MenstrualRecordRepository menstrualRecordRepository;

    @Autowired
    private PersonalConvert personalConvert;

    @Autowired
    private RollbackUtil rollBackUtil;

    /*
    获取基本体征
     */
    public Res<BasicSignVO> getBasicSign(String createUserId) {
        SignInfoPO signInfoPO = signInfoRepository.findByCreateUserId(createUserId).orElse(new SignInfoPO());
        return Res.ok(personalConvert.generateBasicSignVOBySignInfoPO(signInfoPO));
    }

    /*
    修改基本体征
     */
    public Res<OperateVO> updateBasicSign(BasicSignDTO basicSignDTO, String createUserId) {
        SignInfoPO signInfoPO = signInfoRepository.findByCreateUserId(createUserId).orElse(new SignInfoPO());

        // 暂存旧数据
        SignInfoPO oldSignInfoPO = ObjUtil.clone(signInfoPO);

        // 更新数据
        personalConvert.updateSignInfoPOByBasicSignDTO(basicSignDTO, signInfoPO);
        String saveId = signInfoRepository.save(signInfoPO).getId();

        return Res.ok(rollBackUtil.builder("修改基本体征", () -> {
            if (StrUtil.isBlank(oldSignInfoPO.getId())) {
                signInfoRepository.deleteById(saveId);
            } else {
                signInfoRepository.save(oldSignInfoPO);
            }
        }));
    }

    public Res<List<SleepRecordVO>> getSleepRecord(Date startTime, Date endTime, String createUserId) {
        List<SleepRecordPO> sleepRecordPOs = sleepRecordRepository.findAllByCreateUserIdAndCreateDateBetween(createUserId, startTime, DateUtil.endOfDay(endTime));
        List<SleepRecordVO> result = sleepRecordPOs.stream().
                sorted(
                        (o1, o2) -> DateUtil.compare(o1.getCreateDate(), o2.getCreateDate())
                ).map(
                        (entity) -> {
                            SleepRecordVO sleepRecordVO = new SleepRecordVO();
                            sleepRecordVO.setSleepTime(entity.getSleepDuration());
                            sleepRecordVO.setRecordTime(DateUtil.format(entity.getCreateDate(), "yyyy-MM-dd"));
                            return sleepRecordVO;
                        }
                ).toList();
        return Res.ok(result);
    }

    // 新增睡眠记录
    public Res<OperateVO> addSleepRecord(Double duration, String createUserId) {
        SleepRecordPO sleepRecordPO = sleepRecordRepository.findTodayRecord(createUserId).orElse(new SleepRecordPO());

        // 暂存旧数据
        SleepRecordPO oldSleepRecordPO = ObjUtil.clone(sleepRecordPO);

        // 更新数据
        sleepRecordPO.setSleepDuration(duration);
        String saveId = sleepRecordRepository.save(sleepRecordPO).getId();

        return Res.ok(
                rollBackUtil.builder("新增睡眠记录", () -> {
                    if (StrUtil.isBlank(oldSleepRecordPO.getId())) {
                        sleepRecordRepository.deleteById(saveId);
                    } else {
                        sleepRecordRepository.save(oldSleepRecordPO);
                    }
                })
        );
    }


    public Res<List<DrinkRecordVO>> getDrinkRecord(Date startTime, Date endTime, String createUserId) {
        List<DrinkRecordPO> sleepRecordPOs = drinkRecordRepository.findAllByCreateUserIdAndCreateDateBetween(createUserId, startTime, DateUtil.endOfDay(endTime));
        List<DrinkRecordVO> result = sleepRecordPOs.stream().
                sorted(
                        (o1, o2) -> DateUtil.compare(o1.getCreateDate(), o2.getCreateDate())
                ).map(
                        (entity) -> {
                            DrinkRecordVO drinkRecordVO = new DrinkRecordVO();
                            drinkRecordVO.setDrinkVolume(entity.getDrinkVolume());
                            drinkRecordVO.setDrinkTimes(entity.getDrinkTimes());
                            drinkRecordVO.setRecordTime(DateUtil.format(entity.getCreateDate(), "yyyy-MM-dd"));
                            return drinkRecordVO;
                        }
                ).toList();
        return Res.ok(result);
    }

    // 新增睡眠记录
    public Res<OperateVO> addDrinkRecord(Double volume, String createUserId) {
        DrinkRecordPO drinkRecordPO = drinkRecordRepository.findTodayRecord(createUserId).orElse(new DrinkRecordPO());

        // 暂存旧数据
        DrinkRecordPO oldDrinkRecordPO = ObjUtil.clone(drinkRecordPO);

        // 更新数据
        if (StrUtil.isBlank(drinkRecordPO.getId())) {
            drinkRecordPO.setDrinkVolume(volume);
            drinkRecordPO.setDrinkTimes(1);
        } else {
            drinkRecordPO.setDrinkVolume(drinkRecordPO.getDrinkVolume() + volume);
            drinkRecordPO.setDrinkTimes(drinkRecordPO.getDrinkTimes() + 1);
        }
        String saveId = drinkRecordRepository.save(drinkRecordPO).getId();

        return Res.ok(rollBackUtil.builder("新增睡眠记录", () -> {
            if (StrUtil.isBlank(oldDrinkRecordPO.getId())) {
                drinkRecordRepository.deleteById(saveId);
            } else {
                drinkRecordRepository.save(oldDrinkRecordPO);
            }
        }));
    }

    public Res<List<String>> getExerciseType(String createUserId) {
        return Res.ok(exerciseRecordRepository.findDistinctExerciseTypeByCreateUserId(createUserId));
    }

    public Res<List<Map<String, String>>> getExerciseRecord(Date startTime, Date endTime, String createUserId) {
        List<ExerciseRecordPO> exerciseRecordPOs = exerciseRecordRepository.findAllByCreateUserIdAndCreateDateBetween(createUserId, startTime, DateUtil.endOfDay(endTime));
        Map<String, Map<String, String>> tmpMap = new HashMap<>();

        // 按照日期分组
        for (ExerciseRecordPO exerciseRecordPO : exerciseRecordPOs) {
            String date = DateUtil.format(exerciseRecordPO.getCreateDate(), "yyyy-MM-dd");
            Map<String, String> map = tmpMap.computeIfAbsent(date, k -> new HashMap<>());
            map.put(exerciseRecordPO.getExerciseType(), exerciseRecordPO.getExerciseDuration().toString());
        }

        return Res.ok(tmpMap.entrySet().stream().map(
                (entry) -> {
                    entry.getValue().put("recordTime", entry.getKey());
                    return entry.getValue();
                }
        ).toList());
    }

    public Res<OperateVO> addExerciseRecord(String type, Double duration) {
        ExerciseRecordPO exerciseRecordPO = new ExerciseRecordPO();
        exerciseRecordPO.setExerciseType(type);
        exerciseRecordPO.setExerciseDuration(duration);
        String saveId = exerciseRecordRepository.save(exerciseRecordPO).getId();
        return Res.ok(rollBackUtil.builder("新增运动记录", () -> exerciseRecordRepository.deleteById(saveId)));
    }

    //addMenstruationRecord
    public Res<OperateVO> addMenstruationRecord(MenstrualDTO menstrualDTO, String createUserId) {
        // 检测起始时间与结束时间是否有重叠
        Date startDate = menstrualDTO.getStartTime();
        Date endDate = menstrualDTO.getEndTime();
        if (menstrualRecordRepository.existsByCreateUserIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(createUserId, endDate, startDate)) {
            throw new BusinessException("起始时间与结束时间与已有记录有重叠");
        }
        String id = menstrualRecordRepository.save(personalConvert.generateMenstrualRecordPOByMenstrualDTO(menstrualDTO)).getId();
        return Res.ok(rollBackUtil.builder("新增月经记录", () -> menstrualRecordRepository.deleteById(id)));
    }

    public Res<List<Date>> predictMenstruationCycle(String createUserId) {
        List<MenstrualRecordPO> recordPOs = menstrualRecordRepository.findAllByCreateUserIdOrderByStartTimeDesc(createUserId);

        if (recordPOs.size() < 2) {
            throw new BusinessException("月经记录不足，无法预测");
        }

        // 加权计算平均偏移量
        double avgStartOffset = 0;
        double avgEndOffset = 0;

//        for (int i = 0; i < recordPOs.size(); i++) {
//            avgCycle += (recordPOs.size() - i) * DateUtil.between(recordPOs.get(i).getStartTime(), recordPOs.get(i).getEndTime(), DateUnit.DAY);
//        }
//        avgCycle /= (double) (recordPOs.size() * (recordPOs.size() + 1)) / 2;

        for (int i = 0; i < recordPOs.size() - 1; i++) {
            MenstrualRecordPO recordPO = recordPOs.get(i);
            MenstrualRecordPO nextRecordPO = recordPOs.get(i + 1);

            // 加权计算平均偏移量，最近的记录权重最大
            avgStartOffset += (recordPOs.size() - i) * DateUtil.between(recordPO.getStartTime(), nextRecordPO.getStartTime(), DateUnit.DAY);
            avgEndOffset += (recordPOs.size() - i) * DateUtil.between(recordPO.getEndTime(), nextRecordPO.getEndTime(), DateUnit.DAY);
        }

        avgStartOffset /= (double) (recordPOs.size() * (recordPOs.size() + 1)) / 2;
        avgEndOffset /= (double) (recordPOs.size() * (recordPOs.size() + 1)) / 2;

        // 计算下一次月经周期
        Date nextStartTime = DateUtil.offsetDay(recordPOs.get(0).getStartTime(), (int) Math.round(avgStartOffset));
        Date nextEndTime = DateUtil.offsetDay(recordPOs.get(0).getEndTime(), (int) Math.round(avgEndOffset));

        return Res.ok(List.of(nextStartTime, nextEndTime));
    }

    public Res<MenstruationVO> getMenstruationReport(String createUserId) {
        // 半年内记录
        List<MenstrualRecordPO> recordPOs = menstrualRecordRepository.findOneYearRecord(createUserId);


        Map<String, Long> menstruationDayMap = new HashMap<>();
        Map<String, Long> menstruationReactionMap = new HashMap<>();

        for (MenstrualRecordPO recordPO : recordPOs) {
            long cycleLength = DateUtil.between(recordPO.getStartTime(), recordPO.getEndTime(), DateUnit.DAY);

            String month = DateUtil.format(recordPO.getStartTime(), "yyyy-MM");
            if (menstruationDayMap.containsKey(month)) {
                menstruationDayMap.put(month, cycleLength);
            } else {
                menstruationDayMap.put(month, cycleLength);
            }

            boolean hasReaction = false;
            if (recordPO.getConstipation()) {
                hasReaction = true;
                if (menstruationReactionMap.containsKey("便秘")) {
                    menstruationReactionMap.put("便秘", menstruationReactionMap.get("便秘") + cycleLength);
                } else {
                    menstruationReactionMap.put("便秘", cycleLength);
                }
            }
            if (recordPO.getNausea()) {
                hasReaction = true;
                if (menstruationReactionMap.containsKey("恶心")) {
                    menstruationReactionMap.put("恶心", menstruationReactionMap.get("恶心") + cycleLength);
                } else {
                    menstruationReactionMap.put("恶心", cycleLength);
                }
            }
            if (recordPO.getCold()) {
                hasReaction = true;
                if (menstruationReactionMap.containsKey("发冷")) {
                    menstruationReactionMap.put("发冷", menstruationReactionMap.get("发冷") + cycleLength);
                } else {
                    menstruationReactionMap.put("发冷", cycleLength);
                }
            }
            if (recordPO.getIncontinence()) {
                hasReaction = true;
                if (menstruationReactionMap.containsKey("失禁")) {
                    menstruationReactionMap.put("失禁", menstruationReactionMap.get("失禁") + cycleLength);
                } else {
                    menstruationReactionMap.put("失禁", cycleLength);
                }
            }
            if (recordPO.getHot()) {
                hasReaction = true;
                if (menstruationReactionMap.containsKey("潮热")) {
                    menstruationReactionMap.put("潮热", menstruationReactionMap.get("潮热") + cycleLength);
                } else {
                    menstruationReactionMap.put("潮热", cycleLength);
                }
            }
            if (!hasReaction) {
                if (menstruationReactionMap.containsKey("无反应")) {
                    menstruationReactionMap.put("无反应", menstruationReactionMap.get("无反应") + cycleLength);
                } else {
                    menstruationReactionMap.put("无反应", cycleLength);
                }
            }
        }

        MenstruationVO result = new MenstruationVO();
        result.setDays(
                menstruationDayMap.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).
                        sorted(Comparator.comparing(t -> DateUtil.parse(t.getKey(), "yyyy-MM"))).collect(Collectors.toList())
        );
        result.setReactions(
                menstruationReactionMap.entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())).collect(Collectors.toList())
        );
        return Res.ok(result);
    }
}
