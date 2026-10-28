import java.util.Random;
import java.util.List;

public abstract class Jogador extends Personagem {
    @Override
    public boolean atacar( Personagem alvo, Setor setor ) {
        if ( alvo == null )
            throw new IllegalArgumentException( "O alvo não pode ser nulo." );

        if ( setor == null )
            throw new IllegalArgumentException( "O setor não pode ser nulo." );

        if ( !this.estaVivo() )
            return false;

        if ( alvo.estaVivo() )
            if ( setor.ataqueJogadorAcerta() ) {
                alvo.receberDano( this.getAtaque() );
                return true;
            }

        return false;
    }

    public int procurar( Setor setor ) {
        if ( setor == null )
            throw new IllegalArgumentException( "O setor não pode ser nulo." );

        if ( !this.estaVivo() )
            return -1;

        if ( !setor.permiteProcurar() )
            return -1;

        Random random = new Random();
        int dado = random.nextInt( 1, 7 );

        if ( dado <= 3 )
            return dado;
        else if ( dado == 4 ) {
            this.recuperarDefesa( 1 );

            return dado;
        }
        else if ( dado == 5 ) {
            this.recuperarDefesa( 2 );

            return dado;
        }
        else {
            List<Inimigo> inimigosSetor = setor.getInimigos();
            
            for ( Inimigo inimigo : inimigosSetor ) {
                if ( inimigos.estaVivo() )
                    inimigos.receberDano( 1 );
            }
            return dado;
        }
    }

}
