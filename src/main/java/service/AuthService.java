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
    public User autenticar(String nickname, String senha) {
        User user = userRepository.buscarPorNickname(nickname);

        if (user == null) {
            System.out.println("Usuário não encontrado: " + nickname);
            return null;
        }

        if (user.getPassword().equals(senha)) {
            System.out.println("Usuário autenticado: " + nickname);
            return user;
        }

        System.out.println("Senha incorreta para: " + nickname);
        return null;
    }
    
}