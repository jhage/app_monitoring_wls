package br.com.monitoring.wls.utils;

public enum MonitoringType {

        THREAD_DUMP("ThreadStuckDump.dat", new String[] { "Text" }),

        CHANNEL("ChannelRuntime.dat", new String[] { "ChannelName", "MessagesReceivedCount", "MessagesSentCount", "BytesReceivedCount", "BytesSentCount",
                "ConnectionsCount" }),

        WEB_APP("WebAppComponentRuntime.dat", new String[] { "OpenSessionsCurrentCount", "SessionsOpenedTotalCount" }),

        WORK_MANAGER("WorkManagerRuntimes.dat", new String[] { "PendingRequests", "CompletedRequests", "StuckThreadCount" }),

        CLUSTER_DATA("ClusterRuntime.dat", new String[] { "Name", "ResendRequestsCount", "ForeignFragmentsDroppedCount", "FragmentsReceivedCount",
                "FragmentsSentCount", "MulticastMessagesLostCount" }),

        EJB_DATA("EJBComponentRuntime.dat", new String[] { "Name", "AccessTotalCount", "MissTotalCount", "DestroyedTotalCount", "PooledBeansCurrentCount",
                "BeansInUseCurrentCount", "WaiterCurrentCount", "TimeoutTotalCount" }),

        THREAD_POOL("ThreadPoolRuntime.dat", new String[] { "CompletedRequestCount", "ExecuteThreadTotalCount", "ExecuteThreadIdleCount", "HoggingThreadCount",
                "PendingUserRequestCount", "QueueLength", "StandbyThreadCount", "Throughput" }),

        SERVER_JDBC("JDBCDataSourceRuntimeMBeans.dat", new String[] { "Name", "ActiveConnectionsCurrentCount", "WaitSecondsHighCount",
                "WaitingForConnectionCurrentCount", "WaitingForConnectionFailureTotal", "WaitingForConnectionTotal", "WaitingForConnectionHighCount" }),

        SERVER_START("ServerStartDomain.dat", new String[] { "Arguments", "ClassPath", "RootDirectory", "JavaHome" }),

        JVM_RUNTIME("JVMRuntime.dat", new String[] { "HeapFreeCurrent", "HeapFreePercent", "HeapSizeCurrent", "HeapSizeMax", "JavaVersion", "JavaVMVendor" }),

        JROCKIT_RUNTIME("JVMRuntime.dat", new String[] { "HeapFreeCurrent", "HeapFreePercent", "HeapSizeCurrent", "HeapSizeMax", "JavaVersion", "JavaVMVendor",
                "TotalNumberOfThreads", "NumberOfDaemonThreads", "TotalGarbageCollectionTime", "TotalGarbageCollectionCount", "Parallel", "Incremental",
                "Generational", "GCHandlesCompaction" }),

        JMS_SERVER("JMSServerRuntime.dat",
                new String[] { "Name", "MessagesCurrentCount", "MessagesPendingCount", "MessagesHighCount", "MessagesReceivedCount" });

        public String[] strArray;

        public String   filename;

        private MonitoringType(String fileName, String... strArray) {
            this.strArray = strArray;
            this.filename = fileName;
        }

        public boolean equals2(Object obj) {

            if (obj != null) {
                return this.name().equalsIgnoreCase(obj.toString());
            }
            return false;
        }

    }
