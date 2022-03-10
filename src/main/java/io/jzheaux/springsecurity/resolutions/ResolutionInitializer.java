package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResolutionInitializer implements SmartInitializingSingleton {
	private final ResolutionRepository resolutions;
	private final UserRepository users;
	public ResolutionInitializer(ResolutionRepository resolutions, UserRepository users) {
		this.resolutions = resolutions;
		this.users = users;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.resolutions.save(new Resolution("Read War and Peace", "user"));
		this.resolutions.save(new Resolution("Free Solo the Eiffel Tower", "user"));
		this.resolutions.save(new Resolution("Hang Christmas Lights", "user"));
		User user = new User("user",
				"{bcrypt}$2a$10$GN2rFKtvIhkkv4RRqRnOseJ9IxhIU3fiQwGY.nzLCT4.V0rRoKEda");
		user.grantAuthority("resolution:read");
		user.grantAuthority("resolution:write");
		user.grantAuthority("user:read");
		user.setFullName("User Person");
		this.users.save(user);
		User hasread = new User();
		user.setFullName("HasRead Person");
		hasread.setUsername("hasread");
		hasread.setPassword("{bcrypt}$2a$10$GN2rFKtvIhkkv4RRqRnOseJ9IxhIU3fiQwGY.nzLCT4.V0rRoKEda");
		hasread.grantAuthority("resolution:read");
		user.grantAuthority("user:read");
		this.users.save(hasread);

		User haswrite = new User();
		user.setFullName("HasWrite Person");
		haswrite.setUsername("haswrite");
		haswrite.setPassword("{bcrypt}$2a$10$GN2rFKtvIhkkv4RRqRnOseJ9IxhIU3fiQwGY.nzLCT4.V0rRoKEda");
		haswrite.addFriend(hasread);
		haswrite.setSubscription("premium");
		haswrite.grantAuthority("resolution:write");
		user.grantAuthority("user:read");
		this.users.save(haswrite);

		User admin = new User("admin","{bcrypt}$2a$10$hNS8W6kvCNuQWxIUZUUGG.zRTTYdKYdqqzXvCoqjApWVRlj5c0Xke");
		admin.grantAuthority("ROLE_ADMIN");
		user.grantAuthority("user:read");
		user.setFullName("Admin Person");
		//admin.grantAuthority("resolution:read");
		//admin.grantAuthority("resolution:write");
		this.users.save(admin);
	}
}
