package example.symphony.demoapp;
import java.util.HashMap;
import java.util.Map;

import org.finos.symphony.toolkit.spring.app.SymphonyAppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DemoAppController {

  @Autowired
  SymphonyAppProperties appProperties;
 
 
  /** 
  * Uses the new thymeleaf template
  * @return
  */
  @GetMapping("/demo-app-starter.js")
  public ModelAndView getModulesJavascript() {
    Map<String, Object> ctx = new HashMap<>();
    ctx.put("applicationRoot", appProperties.getBaseUrl());
    return new ModelAndView("demo-app-starter.js", ctx);
  }
}