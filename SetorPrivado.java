public class SetorPrivado extends Setor {
    public SetorPrivado(Posicao posicao, boolean[] portas) {
        super(posicao, portas);
    }

    @Override 
    public boolean permiteProcurar() {
        return false;
    }

    @Override 
    public boolean ataqueJogadorAcerta() {
        return true;
    }
    
}
