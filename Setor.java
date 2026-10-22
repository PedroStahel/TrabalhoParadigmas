import java.util.List;
import java.util.ArrayList;

public abstract class Setor {
    private Posicao posicao;
    private boolean[] portas;
    private List<Inimigo> inimigos;
    private boolean visitado;
    private boolean contemFonte;

    protected Setor(Posicao posicao, boolean[] portas) {
        if (posicao == null) {
            throw new IllegalArgumentException("A posição do setor não pode ser nula.");
        }

        if (portas == null || portas.length != 4) {
            throw new IllegalArgumentException("O setor deve possuir exatamente quatro lados.");
        }

        this.posicao = posicao;
        this.portas = portas.clone();
        this.inimigos = new ArrayList<>();
        this.visitado = false;
        this.contemFonte = false;        
    }
    
    public Posicao getPosicao() {
        return this.posicao;
    }

    public boolean[] getPortas() {
        return this.portas.clone();
    }

    public boolean isVisitado() {
        return this.visitado;
    }

    public boolean isContemFonte() {
        return this.contemFonte;
    }

    public void marcarFonte() {
        this.contemFonte = true;
    }

    public void marcarSetorVisitado() {
        this.visitado = true;
    }

    public List<Inimigo> getInimigos() {
        return new ArrayList<>(this.inimigos);
    }

    public void adicionarInimigo(Inimigo inimigo) {
        if (inimigo == null) {
            throw new IllegalArgumentException("O inimigo não pode ser nulo.");
        }
        this.inimigos.add(inimigo);
    }

    public void removerInimigo(Inimigo inimigo) {
        this.inimigos.remove(inimigo);
    }

    public boolean possuiInimigosVivos() {
        for (Inimigo inimigo : this.inimigos) {
            if (inimigo.estaVivo()) {
                return true;
            }
        }
        return false;
    }

    public abstract boolean permiteProcurar();

    public abstract boolean ataqueJogadorAcerta();
}