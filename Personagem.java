public abstract class Personagem {
    private int ataque;
    private int defesa;
    private Posicao posicao;

    public int getAtaque() {
        return this.ataque;
    }

    public int getDefesa() {
        return this.defesa;
    }

    public Posicao getPosicao() {
        return this.posicao;
    }

    public abstract boolean atacar( Personagem alvo, Setor setor ); // Boolean pra ajudar a saber se o ataque foi bem-sucedido, daí pode mostrar mensagem na tela.

    public boolean estaVivo() {
        return this.defesa > 0;
    }

    public void receberDano( int dano ) {
        if ( dano < 0 ) {
            throw new IllegalArgumentException( "O dano não pode ser negativo." );
        }

        if ( dano > this.defesa )
            this.defesa = 0;
        else
            this.defesa -= dano;
    }

    public void setPosicao( Posicao posicao ) {
        if ( posicao == null ) {
            throw new IllegalArgumentException( "A posição não pode ser nula." );
        }

        this.posicao = posicao;
    }

    protected void recuperaDefesa( int quantidade ) { // Serve pra usar na função do jogador, porque a defesa é privada, daí chama na outra função.
        if ( quantidade < 0 ) {
            throw new IllegalArgumentException( "A quantidade de defesa a ser recuperada não pode ser negativa." );
        }

        this.defesa += quantidade;
    }

    protected Personagem( int ataque, int defesa, Posicao posicao ) {
        if ( ataque <= 0 )
            throw new IllegalArgumentException( "O ataque não pode ser negativo ou zero." );
        this.ataque = ataque;

        if ( defesa <= 0 )
            throw new IllegalArgumentException( "A defesa não pode ser negativa ou zero." );
        this.defesa = defesa;

        setPosicao( posicao );
    }

}
