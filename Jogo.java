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
    private String vencedor;

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
        this.vencedor = null;
    }

    public void jogar() {
        System.out.println("Jogo começando");
        while ( !this.jogoAcabou() ){
            System.out.println("===== Ciclo " + this.ciclo + " =====");
            this.turnoJogador(p1, "P1");
            if ( !this.jogoAcabou() )
                this.turnoJogador(p2,"P2");
            if ( !this.jogoAcabou() )
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

    private Direcao lerDirecao() {
        Direcao direcao = null;

        while ( direcao == null ) {
            System.out.print("Direção (N/S/L/O): ");

            try {
                direcao = Direcao.daLetra( this.scanner.nextLine() );
            } catch (ArgumentoInvalido e) {
                System.out.println( e.getMessage() );
            }
        }

        return direcao;
    }

    private int escolherAcao(Setor setor) {
        while ( true ) {
            // mostra só as opções disponíveis
            System.out.println("0 - Passar");
            if ( setor.possuiInimigosVivos() )   
                System.out.println("1 - Atacar");
            if ( setor.permiteProcurar() ) 
                System.out.println("2 - Procurar");
            
            System.out.print("Escolha: ");

            String resposta = scanner.nextLine().trim();

            if ( resposta.equals("1") && setor.possuiInimigosVivos() )   
                return 1;

            if ( resposta.equals("2") && setor.permiteProcurar() )     
                return 2;

            if ( resposta.equals("0") )                 
                return 0;

            System.out.println("Opção inválida ou indisponível.");
        }
    }

    private void acaoProcurar(Jogador jogador, String nome, Setor setor) {
        int dado = jogador.procurar(setor);     

        if ( dado == -1 )
            System.out.println("Não é possível procurar aqui.");
        else if ( dado <= 3 )
            System.out.println(nome + " procurou e não encontrou nada.");
        else if ( dado == 4 )
            System.out.println(nome + " encontrou +1 de DEF! Agora tem " + jogador.getDefesa());
        else if ( dado == 5 )
            System.out.println(nome + " encontrou +2 de DEF! Agora tem " + jogador.getDefesa());
        else
            System.out.println(nome + "encontrou um antivírus! Todos os inimigos do setor perdem 1 de DEF");
    }

    private void acaoAtacar(Jogador jogador, Setor setor) {

    }

    private void acaoCurar(Jogador jogador, Setor setor) {

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

        if ( !setor.possuiInimigosVivos() ) { 
            if ( this.perguntarSimNao("Deseja Mover? (S/N): ") ) { 
                boolean moveu = false;
                boolean querTentar = true;

                while (!moveu && querTentar) {
                    Direcao direcao = lerDirecao();
                    if( this.tabuleiro.moverJogador(jogador, direcao) ) {
                        System.out.println("Jogador " + nome + " foi para " + jogador.getPosicao() );

                        this.tabuleiro.desenhar(jogadores);

                        moveu = true;
                        setor = this.tabuleiro.getSetor(jogador.getPosicao());
                        
                        if ( setor.isContemFonte() ) {
                            this.fonteEncontrada = true;
                            this.vencedor = nome;
                            return;
                        }
                    } else {
                        System.out.println("Não há porta nessa direção!");
                        querTentar = perguntarSimNao("Tentar outra direção? (S/N): ");
                    }
                }
            }
        } else 
            System.out.println("Há inimigos vivos no setor, não é possível sair!");

        for ( int acao = 1; acao <= 2; acao++ ) {
            System.out.println("--- Ação " + acao + " de 2 ---");
            Setor atual = this.tabuleiro.getSetor(jogador.getPosicao());

            int opcao = escolherAcao(atual);

            if ( opcao == 1 )
                acaoAtacar(jogador, atual);
            else if ( opcao == 2 )
                acaoProcurar(jogador, nome, atual);
            else
                System.out.println(nome + " passou a ação.");
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
            System.out.println(this.vencedor + " encontrou a fonte... Os jogadores venceram!");
        else if ( !this.p1.estaVivo() && !this.p2.estaVivo() )
            System.out.println("Os jogadores morreram... Os inimigos venceram!");
        else if ( this.ciclo > MAX_CICLOS )
            System.out.println("Tempo esgotado... Os inimigos venceram!");
    }
}