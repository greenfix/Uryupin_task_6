package ru.uryupin.server;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientSession implements Runnable {

    private static final int CODE_OK = 200;
    private static final int CODE_NOT_PAGE = 404;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            send(readHeader());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param headerIn headerIn
     * @throws IOException IOException
     */
    private void send(String headerIn) throws IOException {
        String[] lines = headerIn.split(System.getProperty("line.separator"));
        String method = lines[0].split(" ")[0];
        int code = (method.equals("GET")) ? CODE_OK : CODE_NOT_PAGE;
        String headerOut = getHeader(code);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(headerOut);
        if (code == CODE_OK) {
            answer.print(getFiles());
        }
    }

    /**
     *
     * @return header
     * @throws IOException IOException
     */
    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;
        while (true) {
            line = reader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            builder.append(line).append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    /**
     *
     * @param code HTTP status code
     * @return header
     */
    private String getHeader(int code) {
        return "HTTP/1.1 " + code + " " + getAnswer(code) + "\n" +
                "Date: " + new Date().toString() + "\n" +
                "Accept-Ranges: none\n" +
                "Content-Type: text/html; charset=utf-8" + "\n" +
                "\n";
    }

    /**
     *
     * @param code HTTP status code
     * @return Answer
     */
    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }

    /**
     *
     * @return files in current directory
     */
    private String getFiles() {
        StringBuilder builder = new StringBuilder();
        File[] directories = new File(new File(".").getAbsolutePath())
                .listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
        assert directories != null;
        for (File dir : directories) {
            builder.append(dir.getAbsolutePath()).append("<br />");
        }

        return builder.toString();
    }
}