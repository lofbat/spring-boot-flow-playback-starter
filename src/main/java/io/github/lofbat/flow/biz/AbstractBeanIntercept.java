package io.github.lofbat.flow.biz;

import io.github.lofbat.flow.dao.InvokeDetailDAO;
import io.github.lofbat.flow.dao.InvokeInfoDAO;
import io.github.lofbat.flow.model.BeanInterceptBO;
import io.github.lofbat.flow.model.BeanInterceptBOConverter;
import io.github.lofbat.flow.utils.MD5Util;
import io.github.lofbat.flow.utils.SerializeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by geqi on 2019/5/29.
 */
abstract class AbstractBeanIntercept {

    protected static final InheritableThreadLocal<ThreadInvokeSign> inheritableThreadLocal = new InheritableThreadLocal<>();

    @Autowired
    protected InvokeDetailDAO invokeDetailDAO;

    @Autowired
    protected InvokeInfoDAO invokeInfoDAO;

    protected BeanInterceptBO buildBeanInterceptBO(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        String uniqueNo = MD5Util.md5(signature.getName()+args.toString());
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(classType);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        String clazzName = clazz.getName();

        BeanInterceptBO beanInterceptBO = BeanInterceptBO.builder()
                .app("app")
                .invokeNo(uniqueNo)
                .className(clazzName)
                .beanName(clazzName)
                .args(SerializeUtil.serializeArray(args))
                .returnValue("")
                .serialNo(0)
                .method(signature.getName())
                .build();

        return beanInterceptBO;
    }

    protected void setThreadInvokeSign(String uniqueNo,Integer serialNo){
        inheritableThreadLocal.set(new ThreadInvokeSign(uniqueNo,serialNo));
    }

    protected void removeThreadInvokeSign(){
        inheritableThreadLocal.remove();
    }

    protected void addThreadInvokeSignSerialNo(){
        ThreadInvokeSign threadInvokeSign = inheritableThreadLocal.get();
        threadInvokeSign.addInvokeTimes();
    }

    protected void saveBeanInterceptBO(BeanInterceptBO beanInterceptBO){
        invokeInfoDAO.insert(BeanInterceptBOConverter.toInvokeInfoDO(beanInterceptBO));
        invokeDetailDAO.insert(BeanInterceptBOConverter.toInvokeDetailDO(beanInterceptBO));
    }

    protected String getUniqueNo(){
        return inheritableThreadLocal.get().getUniqueNo();
    }

    protected Integer getSerialNo(){
        return inheritableThreadLocal.get().getSerialNo();
    }

    @Data
    @AllArgsConstructor
    private class ThreadInvokeSign{

        String uniqueNo;

        Integer serialNo;

        public void addInvokeTimes(){
            serialNo++;
        }
    }
}
