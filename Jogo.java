import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jogo {
    private static final int MAX_CICLOS = 25;

    // atributos
    private Tabuleiro tabuleiro;
    private JogadorSimples p1;
    private JogadorSuporte p2;
    private ArrayList<Jogador> jogadores;
    private Scanner scanner;
    private int ciclo;
    private boolean fonteEncontrada;

    // construtores
    public Jogo() {
        this.tabuleiro = new Tabuleiro();
        Posicao centro = this.tabuleiro.setorCentral().getPosicao();
        this.p1 = new JogadorSimples(centro);
        this.p2 = new JogadorSuporte(centro);
        this.jogadores = new ArrayList<Jogador>();
        this.jogadores.add(p1);
        this.jogadores.add(p2);
        this.scanner = new Scanner(System.in);
        this.ciclo = 1;
        this.fonteEncontrada = false;
    }

    public void jogar() {
        System.out.println("Jogo começando");
        while ( !this.jogoAcabou() ){
            System.out.println("===== Ciclo " + this.ciclo + " =====");
            this.turnoJogador(p1, "P1");
            this.turnoJogador(p2,"P2");
            this.turnoInimigos();
            this.ciclo++;
        } 
        this.mostrarResultado();
    }

    // Método para garantir que em respostas de sim ou nao, o usuario responda S ou N
    // Retorna true para S e false para N
    private boolean perguntarSimNao(String pergunta) {
        String resposta = "";
        while ( !resposta.equalsIgnoreCase("S") && !resposta.equalsIgnoreCase("N") ) {
            System.out.print(pergunta);
            resposta = scanner.nextLine().trim();

            if ( !resposta.equalsIgnoreCase("S") && !resposta.equalsIgnoreCase("N") ) {
                System.out.println("Opção inválida. Digite S ou N.");
            }

        }

        if (resposta.equalsIgnoreCase("S"))
            return true;
        return false;
    }

    private void turnoJogador(Jogador jogador, String nome) {
        // Jogador morto não tem turno
        if ( !jogador.estaVivo() ) {
            System.out.printf("Jogador %s foi eliminado!\n", nome);
            return;
        }
        
        // renderiza o tabuleiro
        System.out.println("Turno de " + nome);  
        this.tabuleiro.desenhar(jogadores);

        Setor setor = this.tabuleiro.getSetor(jogador.getPosicao());

        if ( this.perguntarSimNao("Deseja Mover? (S/N): ") ) { 
            System.out.println("Vou mover");
        }
    }

    private void turnoInimigos() {
        System.out.println("Turno dos inimigos");  
    }

    private boolean jogoAcabou() {
        if ( this.fonteEncontrada || this.ciclo > MAX_CICLOS || (!this.p1.estaVivo() && !this.p2.estaVivo()) )
            return true;
        return false;
    }

    private void mostrarResultado() {
        if ( this.fonteEncontrada )
            System.out.println("A fonte foi encontrada... Os jogadores venceram!");
        else if ( !this.p1.estaVivo() && !this.p2.estaVivo() )
            System.out.println("Os jogadores morreram... Os inimigos venceram!");
        else if ( this.ciclo > MAX_CICLOS )
            System.out.println("Tempo esgotado... Os inimigos venceram!");
    }
}