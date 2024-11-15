package basededados;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/**
 * Esta classe DBHelper e responsavel pelo acesso � base de dados rui que consulta uma tabela de nome: pessoa.
 * Usa como ponte de ligacao a base de dados: jdbc E PRECISA DA DEPENDENCIA: "Jconnector.jar" ESTE FICHEIRO ".jar"
 * (Tamb�m conhecido como Java-ARtefacto -> JAR ) PRECISA DE ESTAR
 * INCLUIDO na Variavel %CLASSPATH% (Windows) ou $CLASSPATH (Linux) @see {@link "https://www.geeksforgeeks.org/import-statement-in-java/" }
 * <p>
 * Se nao for incluido dara #ClassNotFoundException
 * <p>
 * O servico do MySQL deve estar iniciado, senao dara #Exception (erro) que nao obteve resposta.
 * (enviou pacotes de dados, mas nao recebeu nenhum)
 * <p>
 * Ao nivel da camada TCP de rede: se a porta 3306 (porta do servico do Mysql) nao estiver "aberta" 
 * ao longo do routeamento da rede, deve correr esta classe no mesmo Endereco IP, do Motor de Base de Dados MySQL 
 * e substituir na (URL) da Base de Dados o nome da M�quina "ruin" por "localhost" que e a refer�ncia a maquina local (loopback)
 * evitando assim "excepcoes" provenientes de portas bloqueadas na rede.
 * <p>
 * A autenticacao tambem e susceptivel de dar #Exception (erro - "Access Denied"), portanto deve certificar-se de que:
 *      1) Na consola de administracao do motor do MySQL, (MySQL WorkBench ou phpMyAdmin no caso do XAMPP)
 *          Certificar-se que o "username" tem ACESSO e PERMISSAO para EXECUTAR a instrucao SQL em questao.
 *          Neste caso: "select apelido,datanasc,foto,morada,nome,numero from pessoa;"
 *      2) E a "senha" do username fornecida a esta classe esta religiosamente bem escrita.
 *      3) Nesta classe deve adaptar a propriedade #user e a propriedade #pass, conforme a autenticacao que definiu na consola do phpMyAdmin.
 *      
 * @author (Ruin Mantel Die Oliver Prier) 
 * @version (3.0)
 */
public class DBHelper{
    private static final String controlador = "com.mysql.jdbc.Driver";//Precisa do ficheiro "connector.jar" ponte de liga��o ao Mysql
    private static final String bd="jdbc:mysql://ruin/rui"; //URL completa da base de dados que queremos aceder.
    private static final String qry_pessoa = "select apelido,datanasc,foto,morada,nome,numero from pessoa;";//Query a tabela de pessoa
    private static final String user = "rui";//"user" com permiss?o total de acesso (mas basta que tenha acesso de leitura a tabela "pessoa")
    private static final String pass = "rui";//"Pass."'static final' indica que al?m de ser constante e n?o pode ser alterado tambem permanece em mem�ria ap�s a classe ser executada.
    private Connection liga = null; // Inicializa a ligacao @see #liga.
    private Statement sql = null;   //Inicializa��o da declara��o de sql
    private ResultSet res = null;   //Grelha de resultados obtidos com o SELECT a tabela de Pessoa.
    private ArrayList<Pessoa> PessoaS;  //Lista Dinamica (Array Dinamico) que ser� preenchida com os resultados da Query (Consulta a tabela de 'Pessoa') 'SELECT'.
    private int posicao;    //Vari�vel para navega��o nas v�rias pessoas, dentro do array #PessoaS .
    private PreparedStatement preparaSQL = null ; //Prepara��o da instru��o SQL a ser enviada para a tabela
    private String strSQL = "" ; //Palavras est�ticas que ser�o usadas para compor a instru��o SQL, juntamente concatenadas com as vari�veis.
    public DBHelper(){
        PessoaS = new ArrayList<Pessoa>(); //Aloca em mem?ria um array din?mico de Pessoas
        try { 
            Class.forName(controlador);     //precisa do "connector.jar" para carregar o jdbc fornecido pelo fabricante do MySQL
            liga = DriverManager.getConnection(bd,user,pass);//Metodo est?tico que faz a ligacao ao MySQL (liga continua activo depois)
            sql = liga.createStatement();   // Cria uma declara??o para executar uma instru??o SQL
        }
        catch (ClassNotFoundException cnfe){ 
            cnfe.printStackTrace();     //Da "Exception" se o CLASSPATH n?o tiver a refer?ncia ao connector.jar (ou se esta dependencia n?o foi carregada)
            System.err.printf("\n %s \t %s ", cnfe.getLocalizedMessage() , cnfe.getMessage() ) ; //impress�o dos erros num "LOG"
        }
        catch (SQLException sqle){ 
            sqle.printStackTrace();     //Da "Exception" se:1)nome n?o encontrado na rede;2) falta de resposta do servi�o:3306 )autentica??o
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
        }
    }

    /**
     * @deprecated
     * Retorno de MetaDados da Base de dados.
     * <p>
     * @param   tabela              {@link #bd}
     * @return  DatabaseMetaData    {@link #liga}
     */
    @Deprecated public DatabaseMetaData getMetaData(String tabela){
        try { return liga.getMetaData();
        }
        catch (SQLException sqle) { sqle.printStackTrace();
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
            throw new RuntimeException(sqle);
        }
    }

    /**
     * GETTER � este metodo vai � tabela de pessoa fazer uma consulta (SELECT) carregar os seus dados para um CURSOR e transferi-los para uma lista din�mica de pessoas
     * @see #PessoaS (Array dinamico)
     * <p>
     * @return #{@link #PessoaS }
     */
    public ArrayList<Pessoa> queryPessoa(){
        try {
            res = sql.executeQuery(qry_pessoa);                         //retorna um CURSOR (ResultSet) grelha de resultado do comando SQL enviado ao Mysql
            posicao = 0 ;                                               //Posiciona o ArrayList<Pessoa> no inicio.
            while( res.next() ){                                        //Percorre o cursor
                if ( res.getBlob(3) == null )
                    PessoaS.add( new Pessoa( res.getString(1), res.getDate(2), res.getString(4),// chama o constructor
                            res.getString(5), res.getDouble(6) ) );         //da classe pessoa por cada registo do CURSOR e vai a encher o ArrayList
                else
                    PessoaS.add( new Pessoa( res.getString(1), res.getDate(2), res.getBlob(3) ,res.getString(4),// chama o constructor
                        res.getString(5), res.getDouble(6) ) );         //da classe pessoa por cada registo do CURSOR e vai a encher o ArrayList
                posicao++;                                              //passa para a posi??o seguinte do ArrayList<Pessoa> PessoaS (enquanto houver registos no CURSOR)
            }
        }
        catch (SQLException sqle){
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
            sqle.printStackTrace();             //Exception a executar a Query:1)erro de sintaxe SQL (n?o ? validada no java mas ? validada no motor);
        }                                       //2)LOCK da tabela; 3) User sem permiss?o para executar o comando SQL;
        finally{ return PessoaS ;}              //Da #Exception ou nao (erro) retorna o ArrayList, nem que seja null
    }
    boolean inserePessoa( Pessoa p){
        boolean ret = false;
        try{
            strSQL = " INSERT INTO pessoa ( nome,apelido,morada,datanasc,foto ) values ( ?,?,?,?,?) "; //Formato SQL, de forma a prevenir SQL INJECTION
            preparaSQL = liga.prepareStatement(strSQL) ;
            preparaSQL.setString(1, p.getNome() );
            preparaSQL.setString(2, p.getApelido() );
            preparaSQL.setString(3, p.getMorada() );
            preparaSQL.setDate(4, Date.valueOf(p.getDataNasc() ) );
            if ( p.getPath() == null ) preparaSQL.setNull(5, Types.BLOB , "longblob" );
            else {
                InputStream byteS = new FileInputStream(p.getPath());   //N�o inclui a foto no INSERT e poe-o a NULL.
                preparaSQL.setBinaryStream(5, byteS);       //Inclui a Foto no INSERT a tabela de 'pessoa' da Base de Dados.
            }
            preparaSQL.executeUpdate(); // A chave "n�mero" ( como � do tipo: AUTOINCREMENT  no MySQL pode ser omitida no INSERT, o  motor da BD ir� gerar autom�ticamente esse campo)
            ret = true;
        }
        catch( SQLException sqle){
            sqle.printStackTrace(); ret = false;
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
        }
        catch( NullPointerException npe ){
            npe.printStackTrace();
            System.err.printf("\n %s \t %s ", npe.getLocalizedMessage() , npe.getMessage() ) ; //impress�o dos erros num "LOG"
        }
        finally {
            return ret;
        }
    }
    boolean delPessoa( Pessoa p){
        boolean ret = false;
        try{
            sql.executeUpdate("DELETE FROM pessoa where numero =" + p.getNumero() + ";" ) ; //Instru��o SQL padr�o (Standard), mais suscept�vel a ataques de SQL INJECTION
            ret = true;
        }
        catch( SQLException sqle){
            sqle.printStackTrace();
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
        }
        finally {
            return ret;
        }
    }
    boolean updPessoa( Pessoa p){
        boolean ret = false;
        try{
            strSQL = " UPDATE pessoa SET nome=?,apelido=?,morada=?,datanasc=?,foto=? where numero =?; "  ;
            preparaSQL = liga.prepareStatement(strSQL) ;
            preparaSQL.setString(1, p.getNome() );
            preparaSQL.setString(2, p.getApelido() );
            preparaSQL.setString(3, p.getMorada() );
            preparaSQL.setDate(4, Date.valueOf(p.getDataNasc() ) );
            preparaSQL.setDouble(6, p.getNumero() );
            if ( p.getPath() == null ) preparaSQL.setNull(5, Types.BLOB , "longblob" );
            else {
                InputStream byteS = new FileInputStream(p.getPath());
                preparaSQL.setBinaryStream(5, byteS);
            }
            preparaSQL.executeUpdate(); // A chave "n�mero" (como � do tipo: AUTOINCREMENT no MySQL n�o deve ser alterada, pois, � controlada autom�ticamente)
            sql.executeUpdate(strSQL);  // Instru��o REDUNDANTE, apenas para refer�ncia das 2 modalidades para executar SQL � Esta � mais suscet�vel a SQL INJECTION
            ret = true;
        }
        catch( SQLException sqle){
            sqle.printStackTrace();
            System.err.println("\n " + p.getPath() ); //Redirecionamento para a saida de ERROS (por padr�o - � o terminal do monitor)
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
        }
        finally {
            return ret;
        }
    }
    public void fechar(){
        try{
            sql.close();    //Fecha a utiliza�ao de comando SQL (util porque restringe, (um pouco) a infiltracao de SQL INJECTION )
            liga.close();   //Fecha a liga�ao a Base de Dados. e liberta mem?ria ocupada pelo jdbc.
        }
        catch (SQLException sqle){
            sqle.printStackTrace();
            System.err.printf("\n %s \t %s ", sqle.getLocalizedMessage() , sqle.getMessage() ) ; //impress�o dos erros num "LOG"
        }
    }

    /**
     * @deprecated
     * Metodo que e usado apenas para testar esta classe de acesso � base de dados (antes de usar a classe AplicaBD)
     */
    @ Deprecated public static void main(String[] args){ DBHelper a= new DBHelper(); a.queryPessoa(); a.fechar(); }
}

