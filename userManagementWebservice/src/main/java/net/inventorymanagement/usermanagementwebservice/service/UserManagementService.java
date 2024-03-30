package net.inventorymanagement.usermanagementwebservice.service;

import net.inventorymanagement.usermanagementwebservice.dto.UserNameDTO;
import net.inventorymanagement.usermanagementwebservice.model.User;
import net.inventorymanagement.usermanagementwebservice.repository.TeamRepository;
import net.inventorymanagement.usermanagementwebservice.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * User management rest service, responsible for all the program logic.
 */

@Service
@Log4j2
public class UserManagementService {

    @Value("${active.directory.url}")
    private String activeDirectoryUrl;

    @Value("${active.directory.search.base}")
    private String activeDirectorySearchBase;

    @Value("${active.directory.search.filter}")
    private String activeDirectorySearchFilter;

    @Value("${active.directory.binding.user}")
    private String activeDirectoryBindingUser;

    @Value("${path.to.active-directory-binding-pwd}")
    private String pathToActiveDirectoryBindingPwdCsv;

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public UserManagementService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    // GET or create one user, if it doesn't exist already
    public User getUserData(String username) throws Exception {
        User user = userRepository.findByUserLogonName(username);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            return userRepository.saveAndFlush(user);
        } else {
            User activeDirectoryUserData = getActiveDirectoryUserData(username);
            return userRepository.saveAndFlush(
                    new User(
                            activeDirectoryUserData.getUserLogonName(),
                            activeDirectoryUserData.getFirstName(),
                            activeDirectoryUserData.getLastName(),
                            activeDirectoryUserData.getMailAddress(),
                            null,
                            false,
                            false,
                            false,
                            LocalDateTime.now(),
                            activeDirectoryUserData.isActive(),
                            false,
                            false
                    )
            );
        }
    }

    // GET all active directory members
    public List<User> getAllActiveDirectoryUserData() throws Exception {
        List<User> activeDirectoryUserList = new ArrayList<>();
        addAllActiveDirectoryMembersToList(activeDirectoryUserList);
        return activeDirectoryUserList;
    }

    // GET all user data
    // checks if user is super admin and returns all users if true
    public List<User> getAllUsersData(Integer id) {
        User admin = userRepository.findByUserId(id);
        if (admin.isAdmin() || admin.isSuperAdmin()) {
            List<User> allUsers = userRepository.findAll();
            allUsers.sort(Comparator.naturalOrder());
            return allUsers;
        } else {
            return null;
        }
    }

    // GET users username
    public UserNameDTO getUsername(int id) {
        User user = userRepository.findByUserId(id);
        return new UserNameDTO(user.getId(), user.getFirstName(), user.getLastName());
    }

    // GET all users usernames
    public List<UserNameDTO> getAllUsernames(boolean activeUsersOnly) {
        List<User> allUsersList = userRepository.findAll();
        List<UserNameDTO> allUsernamesDTOList = new ArrayList<>();
        for (User user : allUsersList) {
            if (activeUsersOnly && user.isActive()) {
                allUsernamesDTOList.add(new UserNameDTO(user.getId(), user.getFirstName(), user.getLastName()));
            } else {
                allUsernamesDTOList.add(new UserNameDTO(user.getId(), user.getFirstName(), user.getLastName()));
            }
        }
        allUsernamesDTOList.sort(Comparator.naturalOrder());
        return allUsernamesDTOList;
    }

    // GET all usernames from list
    public List<UserNameDTO> getUsernameFromList(List<UserNameDTO> userNameDTOList) {
        for (UserNameDTO userNameDTO : userNameDTOList) {
            User user = userRepository.findByUserId(userNameDTO.getId());
            userNameDTO.setName(user.getFirstName() + " " + user.getLastName());
        }
        userNameDTOList.sort(Comparator.naturalOrder());
        return userNameDTOList;
    }

    // ####################### Active Directory #######################

    private User getActiveDirectoryUserData(String username) throws Exception {
        DirContext context = getActiveDirectoryContext();
        NamingEnumeration<SearchResult> results = getActiveDirectorySearchResult(context);
        while (results.hasMore()) {
            User user = getUserDataFromActiveDirectorySearchResult(results.next());
            if (username.equalsIgnoreCase(user.getUserLogonName())) {
                context.close();
                return user;
            }
        }
        context.close();
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found in Active Directory!");
    }

    private void addAllActiveDirectoryMembersToList(List<User> activeDirectoryUserList) throws Exception {
        DirContext context = getActiveDirectoryContext();
        NamingEnumeration<SearchResult> results = getActiveDirectorySearchResult(context);
        while (results.hasMore()) {
            User user = getUserDataFromActiveDirectorySearchResult(results.next());
            activeDirectoryUserList.add(user);
        }
        context.close();
    }

    private DirContext getActiveDirectoryContext() throws Exception {
        String username = activeDirectoryBindingUser;
        String password = readActiveDirectoryBindingPassword();

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, activeDirectoryUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        return new InitialDirContext(env);
    }

    private String readActiveDirectoryBindingPassword() throws Exception {
        BufferedReader br = new BufferedReader(new java.io.FileReader(pathToActiveDirectoryBindingPwdCsv));
        String password = br.readLine();
        br.close();
        return password;
    }

    private NamingEnumeration<SearchResult> getActiveDirectorySearchResult(DirContext context) throws NamingException {
        String searchBase = activeDirectorySearchBase;
        String searchFilter = activeDirectorySearchFilter;

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String[] attributesToRetrieve = {
                "userPrincipalName",    // user logon name
                "userAccountControl",   // if user is activated or deactivated
                "givenName",            // first name
                "sn",                   // last name
                "mail"                  // e-mail address
        };
        controls.setReturningAttributes(attributesToRetrieve);

        return context.search(searchBase, searchFilter, controls);
    }

    private User getUserDataFromActiveDirectorySearchResult(SearchResult searchResult) throws NamingException {
        Attributes attributes = searchResult.getAttributes();

        Attribute userPrincipalNameAttr = attributes.get("userPrincipalName");
        String userPrincipalName = (userPrincipalNameAttr != null) ? (String) userPrincipalNameAttr.get() : null;
        if (userPrincipalName != null) {
            userPrincipalName = userPrincipalName.split("@")[0];
        }

        Attribute userAccountControlAttr = attributes.get("userAccountControl");
        String userAccountControlValueStr = (String) userAccountControlAttr.get();
        int userAccountControlValue = Integer.parseInt(userAccountControlValueStr);
        boolean isActive = (userAccountControlValue & 2) == 0;

        Attribute firstNameAttr = attributes.get("givenName");
        String firstName = (firstNameAttr != null) ? (String) firstNameAttr.get() : null;

        Attribute lastNameAttr = attributes.get("sn");
        String lastName = (lastNameAttr != null) ? (String) lastNameAttr.get() : null;

        Attribute emailAttr = attributes.get("mail");
        String email = (emailAttr != null) ? (String) emailAttr.get() : null;

        return new User(userPrincipalName, firstName, lastName, email, isActive);
    }

}