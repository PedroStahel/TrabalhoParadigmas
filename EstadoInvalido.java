// Erro para quando o próprio objeto está num estado errado (ex.: o mapa do tabuleiro está quebrado).
// Herda de IllegalStateException, então um catch (IllegalStateException e) também pega ele.
public class EstadoInvalido extends IllegalStateException {
    public EstadoInvalido(String mensagem) {
        super(mensagem);
    }
}
