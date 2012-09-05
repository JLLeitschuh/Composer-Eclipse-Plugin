/*******************************************************************************
 * This file is part of the PHPPackage eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.composer.core.visitor;

import org.eclipse.core.resources.IFile;
import org.pex.core.log.Logger;

import com.dubture.composer.core.model.ComposerFieldNamingStrategy;
import com.dubture.composer.core.model.PHPPackage;
import com.dubture.indexing.core.index.AbstractIndexingVisitor;
import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;

/**
 * 
 * Lucene indexing visitor for composer.json files.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ComposerVisitor extends AbstractIndexingVisitor implements JsonIndexingVisitor
{
    public static final String REFERENCE_ID = "com.dubture.composer.package";
    
    protected Gson gson;
    
    public ComposerVisitor()
    {
        gson = new Gson();
    }
    
    @Override
    public void visit(Object object)
    {
        IFile file = (IFile) getResource();
        
        if (file == null || "composer.json".equals(file.getName()) == false) {
            return;
        }
        
        PHPPackage pHPPackage = (PHPPackage) object;
        
        if (pHPPackage != null) {
            pHPPackage.setFullPath(getResource().getFullPath().removeLastSegments(1).toString());
            String data = gson.toJson(pHPPackage);
            ReferenceInfo info = new ReferenceInfo(REFERENCE_ID, pHPPackage.getName(), data);
            Logger.debug("Adding composer reference " + pHPPackage.getName());
            requestor.addReference(info);
        }        
    }

    @Override
    public FieldNamingStrategy getFieldNamingStrategy()
    {
        return new ComposerFieldNamingStrategy();
    }

    @Override
    public Class<?> getTransformerClass()
    {
        return PHPPackage.class;
    }
}
