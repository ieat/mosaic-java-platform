package mosaic.cloudlet;

import mosaic.core.log.MosaicLogger;

import com.google.common.base.Preconditions;

import eu.mosaic_cloud.components.implementations.basic.BasicComponentHarnessPreMain;

public class CloudletContainerPreMain {

	public static class CloudletContainerParameters {
		public static String classpath;
		public static String configFile;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] arguments) throws Exception {
		Preconditions.checkArgument(arguments != null);
		Preconditions.checkArgument(arguments.length == 2,
				"invalid arguments: <clodlet jar> <cloudlet descriptor>");
		MosaicLogger.getLogger().debug(
				"Container callbacks created with " + arguments[0] + " and "
						+ arguments[1]);
		CloudletContainerPreMain.CloudletContainerParameters.classpath = arguments[0];
		CloudletContainerPreMain.CloudletContainerParameters.configFile = arguments[1];
		BasicComponentHarnessPreMain
				.main(new String[] { "mosaic.cloudlet.runtime.ContainerComponentCallbacks" });

	}

}
