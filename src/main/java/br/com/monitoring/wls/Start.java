package br.com.monitoring.wls;

import br.com.monitoring.wls.monitoring.CompleteWebLogicMonitoring;

public class Start {

	public static void main(String... a) throws Exception {
		CompleteWebLogicMonitoring.main("otp1wl01.internal.timbrasil.com.br", "7007", "capacity", "timbrasil01", ".", "0", "0", "0", "THREAD_POOL");
		CompleteWebLogicMonitoring.main("otp1wl01.internal.timbrasil.com.br", "7007", "capacity", "timbrasil01", ".", "0", "0", "0", "THREAD_DUMP");
	}
}
