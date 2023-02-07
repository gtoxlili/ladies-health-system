package com.system.ladiesHealth.utils.generator;


import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

@SuppressWarnings("unused")
public class NanoIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return NanoIdUtils.randomNanoId();
    }
}
