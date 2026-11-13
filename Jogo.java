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

    private int escolherAcao(Jogador jogador, Setor setor) {
        boolean jogadorPodeCurar = jogador instanceof JogadorSuporte;
        while ( true ) { 
            // mostra só as opções disponíveis
            System.out.println("0 - Passar");
            if ( setor.possuiInimigosVivos() )   
                System.out.println("1 - Atacar");
            if ( setor.permiteProcurar() ) 
                System.out.println("2 - Procurar");
            if ( jogadorPodeCurar ) 
                System.out.println("3 - Curar");
            
            System.out.print("Escolha: ");

            String resposta = scanner.nextLine().trim();

            if ( resposta.equals("1") && setor.possuiInimigosVivos() )   
                return 1;

            if ( resposta.equals("2") && setor.permiteProcurar() )     
                return 2;

            if ( resposta.equals("3") && jogadorPodeCurar )
                return 3;

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

    private void acaoAtacar(Jogador jogador, String nome, Setor setor) {
        List<Inimigo> vivos = new ArrayList<>();
        for ( Inimigo inimigo : setor.getInimigos() ) {
            if ( inimigo.estaVivo() )
                vivos.add(inimigo);
        }

        for ( int i = 0; i < vivos.size(); i++ ) {
            Inimigo inimigo = vivos.get(i);
            System.out.println( (i + 1) + " - Inimigo " + inimigo.getAtaque() + "/" + inimigo.getDefesa() );
        }

        int escolha = 0;
        while ( true ) {
            System.out.print("Qual inimigo atacar? ");
            try {
                escolha = Integer.parseInt( scanner.nextLine().trim() );
                if ( escolha >= 1 && escolha <= vivos.size() )
                    break;                              // se é válido, sai do while
                System.out.println("Número fora da lista.");
            } catch ( NumberFormatException e ) {       // digitou letra
                System.out.println("Digite um número.");
            }
        }
        Inimigo alvo = vivos.get( escolha - 1 );  
        
        boolean acertou = jogador.atacar(alvo, setor);   

        if ( !acertou )
            System.out.println(nome + " errou o ataque!");   // acontece no setor oculto
        else if ( !alvo.estaVivo() )
            System.out.println(nome + " eliminou o inimigo " + escolha);   // eliminou o inimigo
        else
            System.out.println(nome + " acertou o inimigo " + escolha + ". "+ alvo.getDefesa() + " de DEF restante.");  
    }

    private void acaoCurar(JogadorSuporte suporte, String nome) {
        // o P1 só pode ser curado se estiver vivo E no mesmo setor
        boolean podeCurarP1 = this.p1.estaVivo() && this.p1.getPosicao().equals( suporte.getPosicao() );

        System.out.println("1 - Curar a si mesmo (" + nome + ")");
        if ( podeCurarP1 )
            System.out.println("2 - Curar P1");

        Jogador alvo = null;
        while ( alvo == null ) {
            System.out.print("Quem curar? ");
            String resposta = scanner.nextLine().trim();

            if ( resposta.equals("1") )
                alvo = suporte;
            else if ( resposta.equals("2") && podeCurarP1 )
                alvo = this.p1;
            else
                System.out.println("Opção inválida.");
        }

        suporte.usarHabilidade(alvo);
        System.out.println(nome + " curou! DEF agora: " + alvo.getDefesa());
    }

    private void turnoJogador(Jogador jogador, String nome) {
        if ( !jogador.estaVivo() ) { // Jogador morto não tem turno
            System.out.printf("Jogador %s foi eliminado!\n", nome);
            return;
        }
        
       
        System.out.println("Turno de " + nome);  
        this.tabuleiro.desenhar(jogadores); // renderiza o tabuleiro

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

            int opcao = escolherAcao(jogador, atual);

            if ( opcao == 1 )
                acaoAtacar(jogador, nome, atual);
            else if ( opcao == 2 )
                acaoProcurar(jogador, nome, atual);
            else if ( opcao == 3 ) {
                JogadorSuporte suporte = ( JogadorSuporte ) jogador;
                acaoCurar(suporte, nome);
            } 
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