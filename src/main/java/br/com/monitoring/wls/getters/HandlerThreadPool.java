package br.com.monitoring.wls.getters;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.springframework.stereotype.Component;

import weblogic.management.runtime.ExecuteThread;

import br.com.monitoring.wls.writers.Writer;
import br.com.monitoring.wls.utils.Constant;
import br.com.monitoring.wls.utils.MonitoringType;

import java.util.ArrayList;
import java.util.List;

@Component
public class HandlerThreadPool implements Getter {

    private static MonitoringType type = MonitoringType.THREAD_POOL;

    public void execute(MBeanServerConnection connection, Writer writer) throws Exception {

        for (ObjectName serverRuntime : getServerRuntimes(connection)) {
            String name = (String) connection.getAttribute(serverRuntime, "Name");
            String adress = (String) connection.getAttribute(serverRuntime, "ListenAddress");

            ObjectName threadPoolRuntime = (ObjectName) connection.getAttribute(serverRuntime, "ThreadPoolRuntime");

            ExecuteThread[] executeThreadArray = (ExecuteThread[]) connection.getAttribute(threadPoolRuntime,
                    "ExecuteThreads");

            for (ExecuteThread executeThread : executeThreadArray) {

                List<Object> result = new ArrayList<Object>();

                result.add(adress);
                result.add(name);
                result.addAll(getInfo(executeThread.getName(), executeThread.getModuleName(), executeThread.isStandby(),
                        executeThread.isHogger(), executeThread.isStuck()));

                writer.execute(result.toArray());
            }
        }
    }

    private ObjectName[] getServerRuntimes(MBeanServerConnection connection) throws Exception {
        return (ObjectName[]) connection.getAttribute(Constant.SERVICE, "ServerRuntimes");
    }

    private List<Object> getInfo(Object... arrObject) throws Exception {
        List<Object> result = new ArrayList<Object>();

        for (Object obj : arrObject) {
            result.add(obj != null ? obj.toString() : "null");
        }
        return result;
    }

	@Override
	public MonitoringType type() {
		return type;
	}
}