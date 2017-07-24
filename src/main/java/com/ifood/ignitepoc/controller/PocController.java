package com.ifood.ignitepoc.controller;

import com.ifood.ignitepoc.model.MerchantCountResult;
import com.ifood.ignitepoc.model.MerchantStatus;
import com.ifood.ignitepoc.service.PocService;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wagner-matsushita on 13/07/17.
 */
@RestController
@RequestMapping(value = "datagrid")
public class PocController {

    private PocService service;

    @Autowired
    public PocController(PocService service) {
        this.service = service;
    }

    @RequestMapping(value = "/{status}/{time}", method = RequestMethod.GET)
    public MerchantCountResult getValue(@PathVariable MerchantStatus status, @PathVariable String time) {

        return service.countRestaurants(status, LocalTime.parse(time));

    }



}
