/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.springmvc.HandlebarsView;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.consol.citrus.admin.service.ConfigurationService;
import com.consol.citrus.admin.util.FileHelper;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ConfigurationService configService;
    
    @Autowired
    private FileHelper fileHelper;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchProjectHome(@RequestParam("dir") String dir) {
        String directory = fileHelper.decodeDirectoryUrl(dir, configService.getRootDirectory());
        String[] folders = fileHelper.getFolders(directory);

        ModelAndView view = new ModelAndView("FileTree");
        view.addObject("folders", folders);
        view.addObject("baseDir", directory);

        return view;
    }
    
    @RequestMapping(params = {"projecthome"}, method = RequestMethod.GET)
    public String setProjectHome(@RequestParam("projecthome") String projecthome) {
        if (!configService.isProjectHome(projecthome)) {
            throw new IllegalArgumentException("Invalid project home - not a proper Citrus project");
        }
        
        configService.setProjectHome(projecthome);
        return "redirect:/";
    }
}
