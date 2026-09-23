package service;
//Essa classe vai autenticar, registrar e buscar usuarios cadastrados no servidor
import models.User;
import repository.UserRepository;
// Pra a encriptacao usando o Argon2
import com.password4j.Password;
import com.password4j.Hash;
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
        String senha = user.getPassword();
        Hash hash = Password.hash(senha)
                .addRandomSalt()
                .withArgon2();
        String senhaEnc = hash.getResult(); 
        user.setPassword(senhaEnc);
        return userRepository.salvar(user);
    }
    public User autenticar(String nickname, String senha) {
        User user = userRepository.buscarPorNickname(nickname);

        if (user == null) {
            System.out.println("Usuário não encontrado: " + nickname);
            return null;
        }

        if (Password.check(senha, user.getPassword()).withArgon2()) {
            System.out.println("Usuário autenticado: " + nickname);
            return user;
        }

        System.out.println("Senha incorreta para: " + nickname);
        return null;
    }
    
}
