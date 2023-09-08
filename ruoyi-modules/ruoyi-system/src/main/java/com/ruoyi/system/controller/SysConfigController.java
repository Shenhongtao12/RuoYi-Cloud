package com.ruoyi.system.controller;

import com.easy.query.api4j.client.EasyQuery;
import com.easy.query.core.api.pagination.EasyPageResult;
import com.easy.query.core.basic.extension.track.TrackManager;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.SysConfigEntity;
import com.ruoyi.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 参数配置 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private EasyQuery easyQuery;

    /**
     * 获取参数配置列表
     */
    @RequiresPermissions("system:config:list")
    @GetMapping("/list")
    public Object list(SysConfig config) {
        startPage();
//        List<SysConfig> list = configService.selectConfigList(config);
//        return getDataTable(list);
        SysConfigEntity entity = new SysConfigEntity();
        entity.setConfigName("test");
        entity.setConfigKey("test");
        entity.setRemark("------------");
        entity.setConfigValue("ffffffffffffffffaaa");
//        long l = easyQuery.insertable(entity).executeRows(true);
//        System.out.println("自增主键: " + entity.getConfigId());
//

        // 差异化更新
        TrackManager trackManager = easyQuery.getRuntimeContext().getTrackManager();
        try {
            trackManager.begin();
            SysConfigEntity sysConfig = easyQuery.queryable(SysConfigEntity.class).limit(10)
                    .where(o -> o.eq(SysConfigEntity::getConfigId, 100))
                    .firstNotNull("未找到数据");
            easyQuery.addTracking(sysConfig);
            sysConfig.setConfigValue("theme-darktheme-darktheme-darktheme-darktheme-darktheme-darktheme-dark");
            long l = easyQuery.updatable(sysConfig).executeRows();
        } finally {
            trackManager.release();
        }

        EasyPageResult<SysConfigEntity> list = easyQuery.queryable(SysConfigEntity.class).limit(10)
//                .where(o -> o.eq(SysConfigEntity::getConfigId, 3))
                .toPageResult(1, 10);
        return list;
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:config:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config) {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey) {
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @RequiresPermissions("system:config:add")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(SecurityUtils.getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @RequiresPermissions("system:config:edit")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        configService.resetConfigCache();
        return success();
    }

    @GetMapping("deductionInventory")
    public AjaxResult deductionInventory(@RequestParam("name") String name) {
        return success(configService.deductionInventory(name));
    }

    @InnerAuth
    @PostMapping("expirationKey")
    public AjaxResult redisKeyExpiration(@RequestParam("key") String key) {
        configService.resetConfigCache();
        return success();
    }
}
