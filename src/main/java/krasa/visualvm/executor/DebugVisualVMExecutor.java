package krasa.visualvm.executor;

import javax.swing.*;

import com.intellij.execution.executors.DefaultDebugExecutor;
import krasa.visualvm.Resources;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class DebugVisualVMExecutor extends DefaultDebugExecutor {
	@NonNls
	public static final String EXECUTOR_ID = "Debug with VisualVM";
	public static final String DEBUG_WITH_VISUAL_VM = "DebugWithVisualVM";

	@NotNull
	public String getToolWindowId() {
		return getId();
	}

	public Icon getToolWindowIcon() {
		return Resources.DEBUG_13;
	}

	@NotNull
	public Icon getIcon() {
		return Resources.DEBUG;
	}

	public Icon getDisabledIcon() {
		return null;
	}

	public String getDescription() {
		return EXECUTOR_ID;
	}

	@NotNull
	public String getActionName() {
		return DEBUG_WITH_VISUAL_VM;
	}

	@NotNull
	public String getId() {
		return EXECUTOR_ID;
	}

	@NotNull
	public String getStartActionText() {
		return EXECUTOR_ID;
	}

	@Override
	public @NotNull String getStartActionText(@NotNull String configurationName) {
		return getStartActionText();
	}

	public String getContextActionId() {
		// HACK: ExecutorRegistryImpl expects this to be non-null, but we don't want any context actions for every file
		return getId() + " context-action-does-not-exist";
	}

	public String getHelpId() {
		return null;
	}

}
