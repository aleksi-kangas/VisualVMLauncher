package krasa.visualvm.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.jar.JarApplicationConfiguration;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import krasa.visualvm.LogHelper;
import krasa.visualvm.MyConfigurable;
import krasa.visualvm.VisualVMContext;
import krasa.visualvm.VisualVMJavaProgramPatcher;
import krasa.visualvm.executor.DebugVisualVMExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DebugVisualVMRunner extends GenericDebuggerRunner {
	private static final Logger log = Logger.getInstance(DebugVisualVMRunner.class.getName());

	@NotNull
	public String getRunnerId() {
		return DebugVisualVMExecutor.EXECUTOR_ID;
	}

	@Override
	public void execute(@NotNull final ExecutionEnvironment environment)
			throws ExecutionException {
		LogHelper.print("#execute", this);
		final VisualVMGenericDebuggerRunnerSettings debuggerSettings = ((VisualVMGenericDebuggerRunnerSettings) environment.getRunnerSettings());
		debuggerSettings.generateId();
		new VisualVMContext(environment, debuggerSettings).save();

		boolean b = MyConfigurable.openSettingsIfNotConfigured(environment.getProject());
		if (!b) {
			return;
		}
		super.execute(environment);
	}

	public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
		return executorId.equals(DebugVisualVMExecutor.EXECUTOR_ID) && (profile instanceof ModuleRunProfile || profile instanceof JarApplicationConfiguration)
				&& !(profile instanceof RemoteConfiguration);
	}

	@Override
	public VisualVMGenericDebuggerRunnerSettings createConfigurationData(ConfigurationInfoProvider settingsProvider) {
		return new VisualVMGenericDebuggerRunnerSettings();
	}


	@Override
	public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution)
			throws ExecutionException {
		LogHelper.print("#patch", this);
		addVisualVMIdToJavaParameter(javaParameters, settings);
		super.patch(javaParameters, settings, runProfile, beforeExecution);
	}

	/*is called for tomcat, but not normal application*/
	private void addVisualVMIdToJavaParameter(final JavaParameters javaParameters, final RunnerSettings settings) throws ExecutionException {
		final VisualVMGenericDebuggerRunnerSettings debuggerSettings = ((VisualVMGenericDebuggerRunnerSettings) settings);
		LogHelper.print("#doPatch -Dvisualvm.id=" + debuggerSettings.getVisualVMId(), this);
		javaParameters.getVMParametersList().add("-Dvisualvm.id=" + debuggerSettings.getVisualVMId());
		debuggerSettings.setJdkHome(VisualVMJavaProgramPatcher.getJdkPath(javaParameters));
	}

	@Nullable
	@Override
	protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state,
														   @NotNull ExecutionEnvironment environment) throws ExecutionException {
		LogHelper.print("#createContentDescriptor", this);
		addVisualVMIdToJavaParameter(environment, state);
		return super.createContentDescriptor(state, environment);
	}

	/*is called for normal application*/
	private void addVisualVMIdToJavaParameter(ExecutionEnvironment executionEnvironment, RunProfileState runProfileState) throws ExecutionException {
		final VisualVMGenericDebuggerRunnerSettings debuggerSettings = ((VisualVMGenericDebuggerRunnerSettings) executionEnvironment.getRunnerSettings());
		if (runProfileState instanceof JavaCommandLine) {
			final JavaParameters parameters = ((JavaCommandLine) runProfileState).getJavaParameters();
			LogHelper.print("#createContentDescriptor -Dvisualvm.id=" + debuggerSettings.getVisualVMId(), this);
			parameters.getVMParametersList().add("-Dvisualvm.id=" + debuggerSettings.getVisualVMId());
			debuggerSettings.setJdkHome(VisualVMJavaProgramPatcher.getJdkPath(parameters));
		}
	}

	@Nullable
	@Override
	protected RunContentDescriptor attachVirtualMachine(RunProfileState state, @NotNull ExecutionEnvironment env,
														RemoteConnection connection, boolean pollConnection) throws ExecutionException {
		RunContentDescriptor runContentDescriptor = super.attachVirtualMachine(state, env, connection, pollConnection);
		LogHelper.print("#attachVirtualMachine", this);
		RunnerUtils.runVisualVM(this, env, state);
		return runContentDescriptor;
	}

}
