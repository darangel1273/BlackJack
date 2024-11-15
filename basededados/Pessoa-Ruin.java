package basededados;
//# Pacote personalizado
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

/**
 * Esta classe "Pessoa" e uma classe feita para receber os resultados de uma Query (consulta -'select * from pessoa;'). 
 * 
 * @see AplicaBD
 * e criado um ArrayDinamico com elementos desta classe na classe #AplicaDB
 * As variaveis sao privadas (private) para obrigar ao encapsulamento atraves de metodos publicos
 *
 * @author (Ruin Mantel die Oliver Prier)
 * @version (4.0)
 */
public class Pessoa{
    private String Apelido;         //As variaveis sao privadas (private) para obrigar ao encapsulamento atraves de metodos
    private LocalDate DataNasc;     
    private Period Idade;
    private String Morada;  
    private String Nome; 
    private double Numero; 
    private Image Foto;             //Convertido de Blob para imagem no encapsulamento.
    private String Path;
    /**
     * Constructor usado na classe #AplicaDB, para "encher", o Array Dinamico ArrayList<#Pessoa> de nome #PessoaS comm os dados
     * da tabela pessoa da base de dados no mysql.
     * <p>
     * Este constructor chama os respetivos m?todos de encapsulamento de cada uma das propriedades ou atributos desta classe.
     * Encapsulamento necessario para calcular a idade automaticamente assim que tem a data de nascimento, recorrendo à classe #Period
     * Tal como o campo Foto e criado, depois da convers?o do Blob que e trazido da base de dados ( Blob->byte[]->BufferedImage )
     * <p>
     * @param apelido       Apelido da #Pessoa.
     * @param dataNasc      Data de Nascimento da #Pessoa.
     * @param morada        Morada da #Pessoa.
     * @param nome          Nome da #Pessoa.
     * @param numero        Numero individual da #Pessoa (Não utilizado diretamente) mas necessário, pois é a chave prímária na tabela de #Pessoa.
     * @param blb           Blob é o formato com que a #Foto @see #Pessoa é armazenada na Base de Dados (byte[] - array de Bytes) -Carece de conversão para #BufferredImage.
     */
    public Pessoa( String apelido, Date dataNasc, Blob blb, String morada, String nome, double numero ) { //throws Exception {
        setApelido( apelido );  //Chamada ao "Setter" para encapsular o #Apelido @see #Pessoa .
        setDataNasc( dataNasc );//Chamada ao "Setter" para encapsular a #DataNasc @see #Pessoa .
        setFoto( blb );     //Chamada ao "Setter" para encapsular a #Foto ; tem de ser convertido em #Image, (SuperClasse de #BufferedImage).
        setMorada( morada );//Chamada ao "Setter" para encapsular a #Morada @see #Pessoa .
        setNome( nome );    //Chamada ao "Setter" para encapsular o #Nome @see #Pessoa .
        setNumero( numero );//Chamada ao "Setter" para encapsular o #Nome @see #Pessoa .
    }

    /** Construtor igual ao anterior, mas não precisa do #Blob da Fotografia
     * <p>
     * @param apelido       Apelido da #Pessoa.
     * @param dataNasc      Data de Nascimento da #Pessoa.
     * @param morada        Morada da #Pessoa.
     * @param nome          Nome da #Pessoa.
     * @param numero        Numero individual da #Pessoa (Não utilizado diretamente) mas necessário, pois é a chave prímária na tabela de #Pessoa.
     */
    public Pessoa( String apelido, Date dataNasc, String morada, String nome, double numero ) { //throws Exception {
        setApelido( apelido );  //Chamada ao "Setter" para encapsular o #Apelido @see #Pessoa .
        setDataNasc( dataNasc );//Chamada ao "Setter" para encapsular a #DataNasc @see #Pessoa .
        setMorada( morada );//Chamada ao "Setter" para encapsular a #Morada @see #Pessoa .
        setNome( nome );    //Chamada ao "Setter" para encapsular o #Nome @see #Pessoa .
        setNumero( numero );//Chamada ao "Setter" para encapsular o #Nome @see #Pessoa .
    }

    /**
     * Retorno (saida) do Encapsulamento de Apelido. GETTER.
     * <p>
     * @return Apelido String do Apelido.
     */
    public String getApelido() {
        return Apelido;
    }

    /**
     * Encapsulamento (entrada) de Apelido. SETTER.
     * <p>
     * @param   apelido ‘String’ do apelido.
     */
    public void setApelido(String apelido) {
        Apelido = apelido;
    }

    /**
     * Retorno (sa?da) do Encapsulamento da data de nascimento
     * <p>
     * @return Apelido #LocaDate da Data de Nascimento @see #Pessoa.
     */
    public LocalDate getDataNasc() {
        return DataNasc;
    }

    /**
     * @param   dataNasc        Recebe uma data geral e converte numa data regional. ‘SETTER’.
     * Encapsulamento da data de nascimento, #LocalDate (Data com formato regional) 
     * que após possuir este dado, e logo calculada a #Idade
     * <p>
     * @see #setIdade #Date com a data de Nascimento.
     */
    public void setDataNasc(Date dataNasc) {
        DataNasc = dataNasc.toLocalDate();// Conversão para Data Local.
        setIdade(); //SETTER que vai calcular automáticamente a #Idade.
    }

    /**
     *  Metodo privado, so pode ser chamado pelo metodo #setDataNasc. ‘SETTER’.
     *  Após a data de nascimento ter sido encapsulada, a idade e calculada automaticamente.
     *  @see #setDataNasc
     */
    private void setIdade(){
        Idade = Period.between( this.getDataNasc() , LocalDate.now()  ); //Subtrai a data de hoje a data de nascimento.
    }

    /**
     * Retorno (saida) do Encapsulamento da idade em anos (Period — Intervalo de Tempo). GETTER.
     * <p>
     * @return Anos Retorno em anos da #Idade. (Classe de intervalo de Tempo) @see #Period.
     */
    public int getAnos(){
        return Idade.getYears();
    }

    /**
     * Igual ao #getAnos() , mas em vez de anos, retorna meses.
     * @return  meses   {@link #Idade}
     */
    public int getMeses(){ return Idade.getMonths(); }

    /**
     * Retorno (saida) do Encapsulamento da imagem com a fotografia. GETTER.
     * @return      Image retorna a imagem convertida do Blob recebido
     */
    public Image getFoto() {
        return this.Foto.getScaledInstance(250,150, Image.SCALE_SMOOTH );
    }

    /**
     * Metodo que faz a conversao de Blob para uma array de Bytes e depois para uma BufferedImage que da #Foto. GETTER.
     * <p>
     * @param blb ‘Byte’[] — Array de Bytes da Fotografia, Blob e o seu formato guardado na Base de Dados.
     */
    public void setFoto( Blob blb) {    //throws Exception { //Antes de ter o try/catch apenas "transbordava" um erro geral: #Exception
        try{                            // vai TENTAR executar 2 linhas de c?digo suscept?veis de darem erro.
            byte[] ByteS = blb.getBinaryStream().readAllBytes();        //Converte o Blob para um Array de Bytes -> #byte[]
            this.Foto =  Toolkit.getDefaultToolkit().createImage( ByteS );// Convers?o dos ByteS numa BufferedImage
        }
        catch (SQLException | IOException sqle ){ sqle.printStackTrace();
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impressão dos erros num "LOG"
        } //Apanha erro, se o Blob n?o estiver em condi??es, ou inesxixtente.

                                                    //Apanha erro, na impossibilidade de ler o blob e convert?-lo em #ByteS
    }

    /**
     * Metodo que faz a conversao directa de BuufferedImage para Image (Image é superclasse de BufferedImage). SETTER.
     * @param bi    BufferedImage (Variável intermédia para encapsular a Foto da Pessoa)
     */
    public void setBImage(BufferedImage bi){
        this.Foto = bi;
    }
    /**
     * Retorno (saida) do Encapsulamento da #Morada , GETTER.
     * @return Morada String com a morada da Pessoa.
     */
    public String getMorada() {
        return Morada;
    }

    /**
     * Encapsulamento (entrada) de #Morada. SETTER.
     * <p>
     * @param morada ‘String’ com a morada.
     */
    public void setMorada( String morada) {
        Morada = morada;
    }

    /**
     * Retorno (sa?da) do Encapsulamento da #Nome. GETTER.
     * <p>
     * @return #{@return } Nome da Pessoa.
     */
    public String getNome() {
        return Nome;
    }

    /**
     * Encapsulamento (entrada) de #Nome. SETTER .
     * <p>
     * @param nome {@link #Nome} Nome da Pessoa.
     */
    public void setNome( String nome) {
        Nome = nome;
    }

    /**
     * Retorno (saida) do Encapsulamento da #Numero. GETTER.
     * <p>
     * @return Numero   {@link #Numero} Numero da Pessoa.
     */
    public double getNumero() {
        return Numero;
    }

    /**
     * Encapsulamento (entrada) de #Numero (A chave primaria da tabela: "pessoa" da base de dados.) . SETTER.
     * #Numero individual da #Pessoa (Não utilizado diretamente) mas necessário, pois é a chave prímária na tabela de #Pessoa.
     * <p>
     * @param numero {@link #Numero} Chave Primária da Tabela de Pessoa (Não utlizado no Form, mas que é necessário Guardar, para operações de UPDATE e DELETE)
     */
    public void setNumero( double numero ) {
        Numero = numero;
    }

    /**
     * GETTER que guarda o caminho Absoluto de uma imagem, caso seja necessário guardá-la na Base de dados.
     * <p>
     * @return Path {@link #Path} Caminho absoluto até à Imagem, necessário para guardar na Base de dados.
     */
    public String getPath() {
        return Path;
    }

    /**
     * ‘SETTER’ — método que fica com o caminho absoluto até à imagem JPEG (Adquirido na caixa de diálogo que peda a imagem)
     * <p>
     * @see JFileChooser
     * <p>
     * @param path  {@link #Path} Caminho no Sistema Operativo com a localização da Imagem, @see {@link #Foto}
     */
    public void setPath(String path) {
        this.Path = path;
    }
}