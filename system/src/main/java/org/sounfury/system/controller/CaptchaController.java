package org.sounfury.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.lang.UUID;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.sounfury.core.constant.Constants;
import org.sounfury.core.convention.result.Result;
import org.sounfury.core.convention.result.Results;
import org.sounfury.utils.CacheUtils;
import org.sounfury.utils.RedisCache;
import org.sounfury.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@SaIgnore
public class CaptchaController {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Autowired
    private RedisCache redisCache;


  @GetMapping("/captchaImage")
  public Result<Map<String, String>> getCode() throws Exception {
      // 保存验证码信息
      String uuid = UUID.fastUUID().toString();
      String capStr = null;
      BufferedImage image;
      // 生成验证码
      capStr = captchaProducer.createText();
      image = captchaProducer.createImage(capStr);
      // 保存验证码文本到缓存
      String key = Constants.CAPTCHA_CODE_KEY + uuid;

      // 存储验证码文本而不是图片对象
      redisCache.setCacheObject(key, capStr, 60L ,TimeUnit.SECONDS);
      // 返回验证码图片
      FastByteArrayOutputStream os = new FastByteArrayOutputStream();
      ImageIO.write(image, "jpg", os);
      // 返回Base64编码的图片和UUID
      Map<String, String> result = Map.of(
              "uuid", uuid,
              "img", Base64.encode(os.toByteArray())
      );
        return Results.success(result);
  }

}
