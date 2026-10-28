public class JogadorSuporte extends Jogador {
    private static final int ATAQUE = 1;
    private static final int DEFESA = 7;

    public boolean usarHabilidade( Jogador alvo ) {
        if ( alvo == null )
            return false;
        if ( !alvo.estaVivo() )
            return false;
        if ( !this.estaVivo() )
            return false;

        alvo.recuperarDefesa( 2 );
        
        return true;
    }

    public JogadorSuporte( Posicao posIni ) {
        super( ATAQUE, DEFESA, posIni );
    }
}