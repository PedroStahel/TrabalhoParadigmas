// Erro para quando um método recebe um valor errado (ex.: posição nula ou letra inválida).
// Herda de IllegalArgumentException, então um catch (IllegalArgumentException e) também pega ele.
public class ArgumentoInvalido extends IllegalArgumentException {
    public ArgumentoInvalido(String mensagem) {
        super(mensagem);
    }
}
