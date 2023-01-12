package pl.tchorzyksen.my.web.service.service.impl;

import java.util.ArrayList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tchorzyksen.my.web.service.orm.UserEntity;
import pl.tchorzyksen.my.web.service.exception.BadRequestException;
import pl.tchorzyksen.my.web.service.exception.ResourceNotFoundException;
import pl.tchorzyksen.my.web.service.model.dto.UserDto;
import pl.tchorzyksen.my.web.service.repositories.UserRepository;
import pl.tchorzyksen.my.web.service.service.UserService;
import pl.tchorzyksen.my.web.service.shared.Utils;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

  public static final String RESOURCE_NAME = "user";

  public static final String NEW_USER_CREATION_FAILED_EMAIL_ALREADY_EXIST = "Failed to create new user. Email <%s> address already exists.";

  @Autowired
  private UserRepository userRepository;


  @Autowired
  private Utils utils;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public Page<UserDto> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(this::mapToDto);
  }

  @Override
  public UserDto createUser(UserDto userDto) {

    if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
      throw new BadRequestException(String.format(NEW_USER_CREATION_FAILED_EMAIL_ALREADY_EXIST,
          userDto.getEmail()));
    }

    UserEntity userEntity = mapToEntity(userDto);
    userEntity.setUserId(utils.generateUserId(30));
    userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

    log.debug("Save UserEntity: {} to database", userEntity);

    return mapToDto(userRepository.save(userEntity));
  }

  @Override
  public UserDto getUserByEmail(String email) {
    UserEntity userEntity = userRepository.findUserByEmail(email);

    if (userEntity == null) {
      throw new UsernameNotFoundException(email);
    }

    return mapToDto(userEntity);
  }

  @Override
  public UserDto getUserById(@NonNull Long userId) {
    log.debug("Fetch User with id: {}", userId);
    return mapToDto(getUserEntity(userId));
  }

  @Override
  public UserDto updateUser(Long id, UserDto userDto) {
    UserEntity userEntityInDb = getUserEntity(id);

    modelMapper.map(userDto, userEntityInDb);

    log.debug("Save UserEntity {}", userEntityInDb);
    UserEntity savedUserEntity = userRepository.save(userEntityInDb);

    return mapToDto(savedUserEntity);
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    UserEntity userEntity = userRepository.findUserByEmail(email);

    if (userEntity == null) {
      throw new UsernameNotFoundException(email);
    }

    return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
  }

  private UserEntity getUserEntity(Long id) {
    UserEntity userEntity = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    log.debug("UserEntity found {}", userEntity);
    return userEntity;
  }

  private UserEntity mapToEntity(UserDto userDto) {
    log.debug("Map UserDto {} to UserEntity", userDto);
    return modelMapper.map(userDto, UserEntity.class);
  }

  private UserDto mapToDto(UserEntity userEntity) {
    log.debug("Map UserEntity {} to UserDto", userEntity);
    return modelMapper.map(userEntity, UserDto.class);
  }

}
