package io.hpb.contract.controller.admin;

import io.hpb.contract.service.AuthResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/menu")
public class MenuController {

    @Autowired
    private AuthResourceService resourceService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Map<String,Object> list(@RequestBody Map<String,Object> reqParam) throws Exception {
        Map<String, Object> returnParam = resourceService.query(reqParam);
        return returnParam;
    }
}
