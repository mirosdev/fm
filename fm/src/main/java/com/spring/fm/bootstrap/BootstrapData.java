package com.spring.fm.bootstrap;

import com.spring.fm.model.FmUser;
import com.spring.fm.model.Privilege;
import com.spring.fm.model.Role;
import com.spring.fm.repository.FmUserRepository;
import com.spring.fm.repository.PrivilegeRepository;
import com.spring.fm.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@Component
public class BootstrapData implements CommandLineRunner {
    private final FmUserRepository fmUserRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public BootstrapData(FmUserRepository fmUserRepository,
                         PrivilegeRepository privilegeRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder) {
        this.fmUserRepository = fmUserRepository;
        this.privilegeRepository = privilegeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!this.fmUserRepository.existsByEmail("fmuser@fake.com")) {
            this.createUser();
        }

        if (!this.fmUserRepository.existsByEmail("fmadmin@fake.com")) {
            this.createAdminUser();
        }

//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        kpg.initialize(4096, SecureRandom.getInstanceStrong());
//        KeyPair keyPair = kpg.generateKeyPair();
//        Key privateKey = keyPair.getPrivate();
//        Key publicKey = keyPair.getPublic();
//
//        Base64.Encoder encoder = Base64.getEncoder();
//        System.out.println(encoder.encodeToString(privateKey.getEncoded()));
//        System.out.println(encoder.encodeToString(publicKey.getEncoded()));

//        System.out.println(Arrays.toString(privateKey.getEncoded()));
//        System.out.println(Arrays.toString(publicKey.getEncoded()));

//        PKCS8EncodedKeySpec privks = new PKCS8EncodedKeySpec(privateKey.getEncoded());
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey pvt = kf.generatePrivate(privks);
//
//        X509EncodedKeySpec pubks = new X509EncodedKeySpec(publicKey.getEncoded());
//        PublicKey pub = kf.generatePublic(pubks);

//        System.out.println(Arrays.toString(pvt.getEncoded()));
//        System.out.println(Arrays.toString(pub.getEncoded()));
//
//        Base64.Encoder encoder = Base64.getEncoder();
//        System.out.println(encoder.encodeToString(pvt.getEncoded()));
//        System.out.println(encoder.encodeToString(pub.getEncoded()));
//
//        Base64.Decoder decoder = Base64.getDecoder();
//        System.out.println(Arrays.toString(decoder.decode(encoder.encodeToString(pvt.getEncoded()))));
//        System.out.println(Arrays.toString(decoder.decode(encoder.encodeToString(pub.getEncoded()))));
    }

    private void createUser() {
        FmUser fmUser = new FmUser();
        fmUser.setEmail("fmuser@fake.com");
        fmUser.setPassword(this.passwordEncoder.encode("password"));
        Set<Privilege> userPrivileges = Privilege.userPrivilegeSet();
        Set<Role> userRoles = Role.singleUserRole(userPrivileges);
        fmUser.setRoles(userRoles);

        try {
            privilegeRepository.saveAll(userPrivileges);
            roleRepository.saveAll(userRoles);
            fmUserRepository.save(fmUser);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
        }
    }

    private void createAdminUser() {
        FmUser fmUserAdmin = new FmUser();
        fmUserAdmin.setEmail("fmadmin@fake.com");
        fmUserAdmin.setPassword(this.passwordEncoder.encode("12345admin34251pass54321"));
        Set<Privilege> adminPrivileges = Privilege.adminPrivilegeSet();
        Set<Role> adminRoles = Role.singleAdminRole(adminPrivileges);
        fmUserAdmin.setRoles(adminRoles);

        try {
            privilegeRepository.saveAll(adminPrivileges);
            roleRepository.saveAll(adminRoles);
            fmUserRepository.save(fmUserAdmin);
        } catch (Exception e) {
            logger.error(e.toString().concat(Arrays.asList(e.getStackTrace()).toString()));
        }
    }
}
