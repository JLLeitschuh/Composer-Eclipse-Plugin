package com.dubture.composer.ui.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.pdtextensions.core.launch.ScriptLauncher;
import org.pdtextensions.core.launch.execution.ExecutionResponseListener;

import com.dubture.composer.core.log.Logger;
import com.dubture.getcomposer.packages.PharDownloader;

@SuppressWarnings("restriction")
public class CreateProjectJob extends ComposerJob {

	private final String projectName;
	private final String packageName;
	private IPath composerPath;
	private IPath path;
	private JobListener initListener;
	private String packageVersion;
	private boolean startNotified = false;
	private boolean existed;
	private File composerFile;
	
	public CreateProjectJob(IPath path, String projectName, String packageName, String packageVersion) {
		super("Creating composer project");
		this.projectName = projectName;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
		this.path = path;
		
		Logger.debug("Creating new project " + projectName + " from package " + packageName + " / " + packageVersion);
		ResourcesPlugin.getWorkspace();
		DummyProject project = new DummyProject(path);
		setProject(project);
		
		try {
			//TODO: cache the phar file locally
			composerPath = path.append("composer.phar");
			composerFile = composerPath.toFile();
			existed = true;
			if (!composerFile.exists()) {
				existed = false;
				PharDownloader downloader = new PharDownloader();
				InputStream resource = downloader.download();
				FileUtils.copyInputStreamToFile(resource, composerFile);
			}
		} catch (Exception e) {
			Logger.logException(e);
		}
	}

	@Override
	protected void launch(ScriptLauncher launcher) throws ExecuteException, IOException, InterruptedException {

		// cloning large projects can take a long time...
		//TODO: make this configurable via preferences
		launcher.setTimeout(5 * 60000);
		launcher.addResponseListener(new ExecutionResponseListener() {
			
			@Override
			public void executionStarted() {
				
			}
			
			@Override
			public void executionMessage(String message) {
				try {
					if(composerExists() /*message != null && message.equals("Loading composer repositories with package information")*/) {
						notifyOnStart();
					}
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
			
			@Override
			public void executionFinished(String response, int exitValue) {
				notifyOnFinish();
			}
			
			@Override
			public void executionFailed(String response, Exception exception) {
				notifyOnFail();				
			}
			
			@Override
			public void executionError(String message) {
				notifyOnFail();
			}
			
			@Override
			public void executionAboutToStart() {
			}
		});
		
		launcher.launch("create-project", new String[]{"--no-interaction", packageName, projectName, packageVersion});
	}
	
	protected class DummyProject extends Project {

		public DummyProject(IPath path) {
			this(path, (Workspace) ResourcesPlugin.getWorkspace());
		}
		
		protected DummyProject(IPath path, Workspace container) {
			super(path, container);
		}
		
		@Override
		public IResource findMember(String path) {
			return new DummyResource();
		}
		
		@Override
		public IPath getLocation() {
			return path;
		}
	}
	
	private boolean composerExists() {
		IPath projectPath = path.append(projectName).append("composer.json");
		return projectPath.toFile().exists();
	}
	
	public class DummyResource extends org.eclipse.core.internal.resources.File {

		protected DummyResource() {
			super(new Path("/"), null);
		}

		public IPath getFullPath() {
			return new Path("/dummy/composer.phar");
		}
	}

	public void setJobListener(JobListener latch) {
		this.initListener = latch;
	}
	
	private void notifyOnStart() {
		try {
			if (startNotified || initListener == null) {
				return;
			}
			initListener.jobStarted();
			startNotified = true;
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	private void notifyOnFinish() {
		try {
			if (initListener == null) {
				return;
			}
			initListener.jobFinished(projectName);
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	private void notifyOnFail() {
		
		try {
			if (initListener == null) {
				return;
			}
			initListener.jobFailed();
		} catch (Exception e) {
			Logger.logException(e);
		}
	}
	
	public interface JobListener {
		void jobStarted();
		void jobFinished(String projectName);
		void jobFailed();
	}
	
	protected void cleanup() {
		if (existed == false && composerFile != null) {
			composerFile.delete();
		}
	}
}
