public class SetorNormal extends Setor {
    public SetorNormal(Posicao posicao, boolean[] portas) {
        super(posicao, portas);
    }

    @Override 
    public boolean permiteProcurar() {
        return true;
    }

    @Override 
    public boolean ataqueJogadorAcerta() {
        return true;
    }
}