package azarazka.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "user", url = "${user.service.url}")
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/users/{userId}/following")
    ResponseEntity<List<String>> getFollowing(@PathVariable String userId);
}
