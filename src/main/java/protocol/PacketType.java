
//Essa classe vai listar os tipos de pacotes que são aceitos no sistema

public enum PacketType{
    REGISTER, REGISTER_OK, REGISTER_FAIL,
    LOGIN, LOGIN_OK, LOGIN_FAIL, LOGOUT,
    // Autenticação e pedidos/respostas do servidor para o usuario
    MESSAGE,
    CONTACT_LIST, STATUS_UPDATE,
    TYPING_START, TYPING_STOP
    //digitando

}