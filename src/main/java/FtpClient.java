import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FtpClient {

    String password;
    String username;
    String remoteHost;
    Integer port;
    String path;
    String fileName;
    String protocol;
    Logger logger;

    public FtpClient(String password, String username, String remoteHost, Integer port, String path, String fileName, String protocol, Logger logger) {
        this.password = password;
        this.username = username;
        this.remoteHost = remoteHost;
        this.port = port;
        this.path = path;
        this.fileName = fileName;
        this.protocol = protocol;
        this.logger = logger;
    }


    public void getFile() {
        String content = null;
        try {

            if (protocol.equalsIgnoreCase("ftp")) {
                content = getFileToStringUsingFTP();
            } else if (protocol.equalsIgnoreCase("ftps")) {
                content = getFileToStringUsingFTPS();
            } else if (protocol.equalsIgnoreCase("sftp")) {
                content = getFileToStringUsingSFTP();
            }

            if(content != null) {
                salvarArquivo(content);
                logger.info("Dados salvo no arquivo importacao.csv");
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    private String getFileToStringUsingSFTP() throws Exception {
        DefaultSftpSessionFactory sessionFactory = new DefaultSftpSessionFactory();
        try {

            sessionFactory.setPassword(password);
            sessionFactory.setUser(username);
            sessionFactory.setHost(remoteHost);
            sessionFactory.setAllowUnknownKeys(true);

            if(port != null && port > 0) {
                sessionFactory.setPort(port);
            }

            try {
                logger.info("Iniciando conexão SFTP");
                sessionFactory.getSession();
            } catch (Exception e) {
                logger.severe("Erro ao iniciar conexão SFTP");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Listando os arquivos");
                String[] files = sessionFactory.getSession().listNames(path);
                logger.info(Arrays.toString(files));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Obtendo arquivo");
                return getStringFromInputStream(sessionFactory.getSession().readRaw(path+"/"+fileName));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

        } finally {
            sessionFactory.getSession().close();
        }
    }

    private String getFileToStringUsingFTP() throws Exception {
        DefaultFtpSessionFactory sessionFactory = new DefaultFtpSessionFactory();
        try {

            sessionFactory.setPassword(password);
            sessionFactory.setUsername(username);
            sessionFactory.setHost(remoteHost);
            sessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);

            if(port != null && port > 0) {
                sessionFactory.setPort(port);
            }

            try {
                logger.info("Iniciando conexão FTP");
                sessionFactory.getSession();
            } catch (Exception e) {
                logger.severe("Erro ao iniciar conexão FTP");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Listando os arquivos");
                String[] files = sessionFactory.getSession().listNames(path);
                logger.info(Arrays.toString(files));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Obtendo arquivo");
                return getStringFromInputStream(sessionFactory.getSession().readRaw(path+"/"+fileName));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }
        } finally {
            sessionFactory.getSession().close();
        }
    }

    private String getFileToStringUsingFTPS() throws Exception {
        DefaultFtpsSessionFactory sessionFactory = new DefaultFtpsSessionFactory();
        try {

            sessionFactory.setPassword(password);
            sessionFactory.setUsername(username);
            sessionFactory.setHost(remoteHost);
            sessionFactory.setProt(Protocols.PROT);
            sessionFactory.setFileType(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
            sessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
            sessionFactory.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            sessionFactory.setUseClientMode(true);
            sessionFactory.setSessionCreation(true);
            sessionFactory.setImplicit(true);
            sessionFactory.setProtocols(new String[]{Protocols.TLSv1_2});

            if(port != null && port > 0) {
                sessionFactory.setPort(port);
            }

            try {
                logger.info("Iniciando conexão FTPS");
                sessionFactory.getSession();
            } catch (Exception e) {
                logger.severe("Erro ao iniciar conexão FTPS");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Listando os arquivos");
                String[] files = sessionFactory.getSession().listNames(path);
                logger.info(Arrays.toString(files));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }

            try {
                logger.info("Obtendo arquivo");
                return getStringFromInputStream(sessionFactory.getSession().readRaw(path+"/"+fileName));
            } catch (Exception e) {
                logger.severe("Erro ao litar arquivos");
                logger.severe(Arrays.toString(e.getStackTrace()));
                return null;
            }
        } finally {
            sessionFactory.getSession().close();
        }
    }

    private String getStringFromInputStream(InputStream inputStream) {
        return new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public interface Protocols {
        String TLSv1_2 = "TLSv1.2";
        String PROT = "P";
    }

    public void salvarArquivo(String content) {
        try {
            FileWriter myWriter = new FileWriter("importacao.cvs");
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            logger.severe("Erro ao salvar arquivo");
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }

}
