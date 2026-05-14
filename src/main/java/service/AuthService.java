package service;
//Essa classe vai autenticar, registrar e buscar usuarios cadastrados no servidor

import models.User;
import repository.UserRepository;

public class AuthService {
    private UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean registrar(User user){
        User existente = userRepository.buscarPorNickname(user.getNickname());
        if (existente != null) {
            System.out.println("nickname indisponivel: " + user.getNickname());
            return false;
        }
        return userRepository.salvar(user);
    }
}
