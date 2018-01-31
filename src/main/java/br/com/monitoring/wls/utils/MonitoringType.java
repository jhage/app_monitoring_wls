package br.com.monitoring.wls.utils;

public enum MonitoringType {

        THREAD_DUMP("ThreadStuckDump", new String[] { "Text" }),

        CHANNEL("ChannelRuntime", new String[] { "ChannelName", "MessagesReceivedCount", "MessagesSentCount",
                        "BytesReceivedCount", "BytesSentCount", "ConnectionsCount" }),

        WEB_APP("WebAppComponentRuntime", new String[] { "OpenSessionsCurrentCount", "SessionsOpenedTotalCount" }),

        WORK_MANAGER("WorkManagerRuntimes",
                        new String[] { "PendingRequests", "CompletedRequests", "StuckThreadCount" }),

        CLUSTER_DATA("ClusterRuntime", new String[] { "Name", "ResendRequestsCount", "ForeignFragmentsDroppedCount",
                        "FragmentsReceivedCount", "FragmentsSentCount", "MulticastMessagesLostCount" }),

        EJB_DATA("EJBComponentRuntime",
                        new String[] { "Name", "AccessTotalCount", "MissTotalCount", "DestroyedTotalCount",
                                        "PooledBeansCurrentCount", "BeansInUseCurrentCount", "WaiterCurrentCount",
                                        "TimeoutTotalCount" }),

        THREAD_POOL("ThreadPoolRuntime",
                        new String[] { "CompletedRequestCount", "ExecuteThreadTotalCount", "ExecuteThreadIdleCount",
                                        "HoggingThreadCount", "PendingUserRequestCount", "QueueLength",
                                        "StandbyThreadCount", "Throughput" }),

        EXECUTE_THREAD("ExecuteThreadRuntime", new String[] { "Name", "ModuleName", "Standby", "Hogger", "Stuck" }),

        SERVER_JDBC("JDBCDataSourceRuntime",
                        new String[] { "Name", "ActiveConnectionsCurrentCount", "WaitSecondsHighCount",
                                        "WaitingForConnectionCurrentCount", "WaitingForConnectionFailureTotal",
                                        "WaitingForConnectionTotal", "WaitingForConnectionHighCount" }),

        SERVER_START("ServerStartDomain", new String[] { "Arguments", "ClassPath", "RootDirectory", "JavaHome" }),

        JVM_RUNTIME("JVMRuntime", new String[] { "HeapFreeCurrent", "HeapFreePercent", "HeapSizeCurrent", "HeapSizeMax",
                        "JavaVersion", "JavaVMVendor" }),

        JROCKIT_RUNTIME("JVMRuntime",
                        new String[] { "HeapFreeCurrent", "HeapFreePercent", "HeapSizeCurrent", "HeapSizeMax",
                                        "JavaVersion", "JavaVMVendor", "TotalNumberOfThreads", "NumberOfDaemonThreads",
                                        "TotalGarbageCollectionTime", "TotalGarbageCollectionCount", "Parallel",
                                        "Incremental", "Generational", "GCHandlesCompaction" }),

        JMS_SERVER("JMSServerRuntime", new String[] { "Name", "MessagesCurrentCount", "MessagesPendingCount",
                        "MessagesHighCount", "MessagesReceivedCount" });

        public String[] strArray;

        public String filename;

        private MonitoringType(String fileName, String... strArray) {
                this.strArray = strArray;
                this.filename = fileName;
        }

        @Override
        public String toString() {
                return filename;
        }
}
