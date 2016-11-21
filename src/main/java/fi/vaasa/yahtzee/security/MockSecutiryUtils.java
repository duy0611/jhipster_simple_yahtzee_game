package fi.vaasa.yahtzee.security;

import fi.vaasa.yahtzee.domain.dto.UserDTO;

/**
 * Method to by-pass security by creating fake authentication account
 */
public class MockSecutiryUtils {
	
	public static final String NO_PASSWORD = "N/A";

	public static UserDTO createDummyUserAccount(String loginName) {
		UserDTO userDto = new UserDTO(loginName);
		
		if(loginName.equals("admin")) {
			userDto.setEmail("admin@localhost");
			userDto.setFirstName("Admin");
			userDto.setLastName("User");
			userDto.getAuthorities().add(AuthoritiesConstants.ADMIN);
			userDto.getAuthorities().add(AuthoritiesConstants.USER);
		}
		else {
			userDto.setEmail("user@localhost");
			userDto.setFirstName(loginName.toUpperCase());
			userDto.setLastName("User");
			userDto.getAuthorities().add(AuthoritiesConstants.USER);
		}
		return userDto;
	}
	
}
