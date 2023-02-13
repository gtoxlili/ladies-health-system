package com.system.ladiesHealth.domain.pojo.openAI;


import lombok.Data;

import java.lang.reflect.Field;
import java.util.Optional;

@Data
public class ModerationsRes {

    private String id;
    private String model;
    private ModerationsResResults[] results;

    @Data
    public static class ModerationsResResults {
        private boolean flagged;
        private ModerationsResResultsCategories categories;
        private ModerationsResResultsCategoryScores category_scores;

        @Data
        public static class ModerationsResResultsCategories {
            private boolean sexual;
            private boolean hate;
            private boolean violence;
            private boolean self_harm;
            private boolean sexual_minors;
            private boolean hate_threatening;
            private boolean violence_graphic;
        }

        @Data
        public static class ModerationsResResultsCategoryScores {
            private double sexual;
            private double hate;
            private double violence;
            private double self_harm;
            private double sexual_minors;
            private double hate_threatening;
            private double violence_graphic;

            /**
             * 获取置信度最高的分类
             */
            public String getHighestCategory() {
                // 通过反射获取所有字段
                Field[] fields = this.getClass().getDeclaredFields();
                // 用于存储最高置信度的字段名
                String highestCategory = null;
                // 用于存储最高置信度
                double highestScore = 0;
                for (Field field : fields) {
                    try {
                        // 获取字段名
                        String fieldName = field.getName();
                        // 获取字段值
                        double fieldValue = (double) field.get(this);
                        // 如果字段值大于最高置信度,则更新最高置信度
                        if (fieldValue > highestScore) {
                            highestScore = fieldValue;
                            highestCategory = fieldName;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return highestCategory;
            }
        }
    }

    /**
     * 是否违规,如有则返回违规点
     *
     * @return Optional<String>
     */
    public Optional<String> getViolation() {
        if (results == null || results.length == 0) {
            return Optional.empty();
        }
        ModerationsResResults result = results[0];
        return result.isFlagged() ? Optional.of(result.getCategory_scores().getHighestCategory()) : Optional.empty();
    }

}
