package mitya.haha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StaticController {

    @RequestMapping(value="/", method = RequestMethod.GET, produces = "text/html")
    public String index(){
        return "index.html";
    }
}
