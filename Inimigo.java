import java.util.Random;

public class Inimigo extends Personagem {
    @Override
    public boolean atacar( Personagem alvo, Setor setor ) {
        if ( alvo == null ) {
            throw new IllegalArgumentException( "O alvo não pode ser nulo." );
        }

        if ( setor == null ) {
            throw new IllegalArgumentException( "O setor não pode ser nulo." );
        }

        if ( !this.estaVivo() )
            return false;

        if ( alvo.estaVivo() )
            if ( this.ataqueAcerta() ) {
                alvo.receberDano( this.getAtaque() );

                return true;
            }

        return false;
    }

    public boolean ataqueAcerta() {
        Random random = new Random();
        int dado = random.nextInt( 1, 7 );

        return dado % 2 == 0;
    }

    private static int sortearAtributos() {
        Random random = new Random();
        return random.nextInt( 1, 4 );
    }
    
    public Inimigo( Posicao posicao ) {
        this( sortearAtributos(), posicao );
    }

    private Inimigo( int atributos, Posicao posicao ) {
        super( atributos, atributos, posicao );
    }
}
