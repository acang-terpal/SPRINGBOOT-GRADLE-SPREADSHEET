/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import entity.EntityConfig;
import implement.ImpleConfig;
import interfc.InterfcConfig;


/**
 *
 * @author hasan
 */
public class ControllerConfig {
    InterfcConfig service;
    EntityConfig conf;
    String workDir;
    public ControllerConfig(EntityConfig conf, String workDir){
        this.service =  (InterfcConfig) new ImpleConfig();
        this.conf = conf;
        this.workDir = workDir;
    }
    public EntityConfig getData() throws ClassNotFoundException{
        this.conf = this.service.getData(this.conf,this.workDir);
        return this.conf;
    }
}
