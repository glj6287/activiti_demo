package com.yoland.activiti.init;

  
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
  
/** 
 * @Description:结合Spring自动部署Activit流程定义文件 
 * @version：2012-12-20 
 * @author： hywin 
 */  
public class WorkflowDeployer implements InitializingBean,ApplicationContextAware {  
  
	private static Logger logger = LoggerFactory.getLogger(WorkflowDeployer.class);
	
    private Resource[] deploymentResources;  
    private String category;  
    ApplicationContext appCtx;  
  
    public void setDeploymentResources(Resource[] resources) {  
        this.deploymentResources = resources;  
    }  
  
    public void setCategory(String category) {  
        this.category = category;  
    }  
  
    @Override  
    public void setApplicationContext(ApplicationContext applicationContext)  
            throws BeansException {  
        this.appCtx = applicationContext;  
    }  
  
    @Override  
    public void afterPropertiesSet() throws Exception {  
        if (category == null) {  
            throw new FatalBeanException("缺失属性 : category");  
        }  
        if (deploymentResources != null) {  
            RepositoryService repositoryService = appCtx  
                    .getBean(RepositoryService.class);  
            for (Resource r : deploymentResources) {  
                String deploymentName= r.getFilename().substring(0,r.getFilename().lastIndexOf("."));
                logger.info("文件资源名称："+deploymentName);
                String resourceName ="activiti/" + r.getFilename();  
                boolean doDeploy = true;  
                List<Deployment> deployments = repositoryService  
                        .createDeploymentQuery().deploymentName(deploymentName)  
                        .orderByDeploymenTime().desc().list();  
                if (!deployments.isEmpty()) {  
                    Deployment existing = deployments.get(0);  
                    try {  
                        InputStream in = repositoryService.getResourceAsStream(  
                                existing.getId(), resourceName);  
                        if (in != null) {  
                            File f = File.createTempFile(  
                                    "deployment",  
                                    "xml",  
                                    new File(System  
                                            .getProperty("java.io.tmpdir")));  
                            f.deleteOnExit();  
                            OutputStream out = new FileOutputStream(f);  
                            IOUtils.copy(in, out);  
                            in.close();  
                            out.close();  
                            doDeploy = (FileUtils.checksumCRC32(f) != FileUtils  
                                    .checksumCRC32(r.getFile()));  
                        } else  
                            throw new ActivitiException("不能读取资源 "  
                                    + resourceName + ", 输入流为空");  
                    } catch (ActivitiException ex) {  
                        logger.error("Unable to read " + resourceName  
                                + " of deployment " + existing.getName()  
                                + ", id: " + existing.getId()  
                                + ", will re-deploy");  
                    }  
                }  
                if (doDeploy) {  
                	String png ="activiti/"+ r.getFilename().substring(0,r.getFilename().lastIndexOf("."))+".png";
                	logger.info("流程图相对路径："+png);
                    Deployment deploy = repositoryService.createDeployment().name(deploymentName)  
                            .addInputStream(resourceName, r.getInputStream())
                            .addInputStream(png, r.getInputStream())  
                            .deploy();  
                    logger.info("文件部署成功 : " + r.getFilename()+"自动部署的ID:"+deploy.getId());  
                }  
            }  
        }  
    }  
    
}  