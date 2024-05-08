package azarazka.authenticationservice.client;

import azarazka.authenticationservice.dto.AuthRequest;
import azarazka.authenticationservice.dto.LoginUserDto;
import azarazka.authenticationservice.dto.RegisterUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user", url = "${user.service.url}")
public interface UserClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/users")
    ResponseEntity<RegisterUser> registerUser(@RequestBody AuthRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/api/users/auth")
    ResponseEntity<RegisterUser> authenticate(@RequestBody LoginUserDto loginUserDto);
}