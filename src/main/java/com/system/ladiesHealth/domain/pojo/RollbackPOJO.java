package com.system.ladiesHealth.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class RollbackPOJO {

    public interface Action {
        void apply() throws Exception;
    }

    private String action;

    public String getAction() {
        return action;
    }

    private Action rollback;

    public void rollBack() throws Exception {
        rollback.apply();
    }
}
