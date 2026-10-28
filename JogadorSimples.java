public class JogadorSimples extends Jogador {
    private static final int ATAQUE = 2;
    private static final int DEFESA = 6;

    public JogadorSimples( Posicao posIni ) {
        super( ATAQUE, DEFESA, posIni );
    }
}