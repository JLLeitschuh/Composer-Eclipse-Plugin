package com.dubture.composer.eclipse.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.getcomposer.core.PackageInterface;
import org.pdtextensions.core.model.InstallableItem;

public class EclipsePHPPackage implements
        NamespaceResolverInterface, InstallableItem
{
    private final PackageInterface phpPackage;
    

    public EclipsePHPPackage(PackageInterface phpPackage) {
        
        Assert.isNotNull(phpPackage);
        this.phpPackage = phpPackage;
    }

    @Override
    public IPath resolve(IResource resource)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName()
    {
        return phpPackage.getName();
    }

    @Override
    public String getDescription()
    {
        return phpPackage.getDescription();
    }

    @Override
    public String getUrl()
    {
        return phpPackage.getUrl();
    }

    public void setFullPath(String string)
    {
        // TODO Auto-generated method stub

    }

    public IPath getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PackageInterface getPhpPackage()
    {
        return phpPackage;
    }

}
