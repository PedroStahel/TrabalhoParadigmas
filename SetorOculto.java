import java.util.Random;

public class SetorOculto extends Setor {
    public SetorOculto(Posicao posicao, boolean[] portas) {
        super(posicao, portas);
    }

    @Override 
    public boolean permiteProcurar() {
        return true;
    }

    @Override 
    public boolean ataqueJogadorAcerta() {
        Random random = new Random();
        boolean acertou = random.nextBoolean();
        return acertou;
    }
}