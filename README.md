# 🛡️ Antivírus por um Dia

Jogo cooperativo de terminal desenvolvido em **Java** para a disciplina **CI1062 — Paradigmas de Programação**, da **Universidade Federal do Paraná (UFPR)**, ministrada pela Prof.ª Rachel Reis.

Dois jogadores exploram um tabuleiro, enfrentam vírus e tentam encontrar a fonte da infecção antes que o tempo acabe.

## 🎮 Sobre o jogo

- **Tabuleiro:** 5 × 5 setores, com os jogadores começando no centro.
- **Objetivo:** encontrar a fonte de infecção (`X`).
- **Inimigos:** ao entrar pela primeira vez em um setor, surgem de 1 a 3 vírus; setores já visitados não geram novos inimigos.
- **Duração:** até 25 ciclos, cada um composto pelos turnos de P1, P2 e dos inimigos.
- **Fim de jogo:** a fonte é encontrada, os 25 ciclos se esgotam ou ambos os jogadores morrem.

### Personagens

| Personagem | ATK | DEF | Característica |
|---|---:|---:|---|
| **P1 — Jogador simples** | 2 | 6 | Pode mover, atacar e procurar. |
| **P2 — Jogador de suporte** | 1 | 7 | Também pode recuperar 2 pontos de DEF de si ou de P1, se estiverem no mesmo setor. |
| **Inimigo (vírus)** | 1–3 | 1–3 | ATK = DEF, definidos por sorteio; ataques acertam com 50% de chance. |

### Tipos de setor

| Tipo | Ataque do jogador | Procura |
|---|---|---|
| **Normal** | Sempre acerta | Permitida |
| **Oculto** | Acerta com 50% de chance | Permitida |
| **Privado** | Sempre acerta | Não permitida |

### Ações e turnos

Cada jogador pode realizar **duas ações** de ataque e/ou procura por turno. Ao procurar, um dado de seis faces determina o efeito:

| Dado | Resultado |
|---|---|
| 1–3 | Nada encontrado |
| 4 | Recupera 1 ponto de DEF |
| 5 | Recupera 2 pontos de DEF |
| 6 | Todos os inimigos vivos do setor perdem 1 ponto de DEF |

Após os turnos dos jogadores, os inimigos vivos atacam os jogadores presentes nos respectivos setores.

**Premissas adotadas pelo grupo:**

- Mover não consome uma das duas ações; cada jogador pode mover no máximo uma vez por turno, antes das ações.
- Só é possível sair de um setor sem inimigos vivos.
- A habilidade de cura do suporte substitui as duas ações do seu turno.
- A fonte de infecção (`X`) aparece no mapa desde o início, como nas figuras do enunciado.

## 🚀 Como executar

**Requisito:** JDK **17 ou superior** (Java 21 recomendado).

```bash
git clone https://github.com/PedroStahel/TrabalhoParadigmas.git
cd TrabalhoParadigmas
javac *.java
java Main
```

Para conferir a versão do Java instalada:

```bash
java -version
javac -version
```

> O código utiliza `Random.nextInt(origem, limite)`, disponível a partir do Java 17.

## 🧩 Estrutura do projeto

| Grupo | Classes | Responsabilidade |
|---|---|---|
| **Personagens** | `Personagem`, `Jogador`, `JogadorSimples`, `JogadorSuporte`, `Inimigo`, `Curavel` | Ataque, defesa, procura e cura |
| **Cenário** | `Setor`, `SetorNormal`, `SetorOculto`, `SetorPrivado`, `Tabuleiro` | Setores, portas, inimigos e mapa |
| **Apoio** | `Posicao`, `Direcao` | Coordenadas e direções |
| **Controle** | `Jogo`, `Main` | Ciclos, turnos, ações e início da partida |

### Orientação a objetos aplicada

- **Encapsulamento:** atributos privados e alterações controladas por métodos; `Setor.getInimigos()` retorna uma cópia da lista.
- **Herança:** `Jogador` e `Inimigo` estendem `Personagem`; os três tipos de setor estendem `Setor`.
- **Classes abstratas:** `Personagem`, `Jogador` e `Setor` compartilham estrutura e comportamento.
- **Interface:** `Curavel` declara `recuperarDefesa(int)`, implementado por `Jogador`.
- **Polimorfismo de inclusão:** o comportamento de `ataqueJogadorAcerta()` depende do tipo concreto de setor.
- **Polimorfismo paramétrico:** uso de coleções genéricas, como `List<Jogador>` e `List<Inimigo>`.
- **Sobrecarga:** construtores de `Inimigo` com assinaturas diferentes.
- **Coleções:** `List` e `ArrayList` organizam jogadores, inimigos e setores.

## 🧪 Desenvolvimento e testes

O grupo realizou revisões do código e testes automatizados, incluindo **200 partidas simuladas**, para verificar travamentos e exceções.

**ChatGPT** e **Claude** foram utilizados como apoio ao estudo de Java e orientação a objetos, à revisão do código, aos testes e à elaboração do relatório e do diagrama de classes. Cada integrante escreveu sua parte do código, que foi revisada pelo grupo.

## 👥 Integrantes

- **Bruno Bertussi Camillo** — GRR20252947
- **Lucas Mazurkievicz Sekikawa** — GRR20255726
- **Aaron Moura** — GRR20255804
- **Pedro Stahel de Azevedo** — GRR20252084

---

**UFPR · CI1062 — Paradigmas de Programação**

Enunciado original: [`TrabalhoPratico.md`](TrabalhoPratico.md) · [`TrabalhoPratico.pdf`](TrabalhoPratico.pdf)
