import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (t.type != TokenType.EOF);
        return tokens;
    }

    private Token nextToken() {
        skipWhitespace();

        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "");
        }

        char c = input.charAt(pos);

        // Identificadores ou palavras-chave
        if (Character.isLetter(c) || c == '_') {
            return identifier();
        }

        // Números (int ou real)
        if (Character.isDigit(c)) {
            return number();
        }

        // Símbolos e operadores
        switch (c) {
            case '+':
                pos++;
                return new Token(TokenType.PLUS, "+");
            case '-':
                pos++;
                return new Token(TokenType.MINUS, "-");
            case '*':
                pos++;
                return new Token(TokenType.STAR, "*");
            case '/':
                // Comentário de linha: // ...
                if (peekNext() == '/') {
                    pos += 2;
                    while (pos < input.length() && input.charAt(pos) != '\n') pos++;
                    return nextToken();
                } else {
                    pos++;
                    return new Token(TokenType.SLASH, "/");
                }
            case '=':
                if (peekNext() == '=') {
                    pos += 2;
                    return new Token(TokenType.EQUAL_EQUAL, "==");
                } else {
                    pos++;
                    return new Token(TokenType.ASSIGN, "=");
                }
            case '!':
                if (peekNext() == '=') {
                    pos += 2;
                    return new Token(TokenType.BANG_EQUAL, "!=");
                }
                throw error("Caractere '!' inesperado.");
            case '>':
                if (peekNext() == '=') {
                    pos += 2;
                    return new Token(TokenType.GREATER_EQUAL, ">=");
                } else {
                    pos++;
                    return new Token(TokenType.GREATER, ">");
                }
            case '<':
                if (peekNext() == '=') {
                    pos += 2;
                    return new Token(TokenType.LESS_EQUAL, "<=");
                } else {
                    pos++;
                    return new Token(TokenType.LESS, "<");
                }
            case ';':
                pos++;
                return new Token(TokenType.SEMICOLON, ";");
            case '(':
                pos++;
                return new Token(TokenType.LPAREN, "(");
            case ')':
                pos++;
                return new Token(TokenType.RPAREN, ")");
            case '{':
                pos++;
                return new Token(TokenType.LBRACE, "{");
            case '}':
                pos++;
                return new Token(TokenType.RBRACE, "}");
            default:
                throw error("Caractere inválido: '" + c + "'");
        }
    }

    private Token identifier() {
        int start = pos;
        while (pos < input.length() &&
               (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
            pos++;
        }
        String text = input.substring(start, pos);

        switch (text) {
            case "int":   return new Token(TokenType.INT, text);
            case "real":  return new Token(TokenType.REAL, text);
            case "if":    return new Token(TokenType.IF, text);
            case "else":  return new Token(TokenType.ELSE, text);
            case "while": return new Token(TokenType.WHILE, text);
            case "print": return new Token(TokenType.PRINT, text);
            case "input": return new Token(TokenType.INPUT, text);
            default:      return new Token(TokenType.IDENTIFIER, text);
        }
    }

    private Token number() {
        int start = pos;
        boolean hasDot = false;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                pos++;
            } else {
                break;
            }
        }
        String text = input.substring(start, pos);
        return new Token(TokenType.NUMBER, text);
    }

    private void skipWhitespace() {
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else {
                break;
            }
        }
    }

    private char peekNext() {
        if (pos + 1 >= input.length()) return '\0';
        return input.charAt(pos + 1);
    }

    private RuntimeException error(String msg) {
        return new RuntimeException("Erro léxico em posição " + pos + ": " + msg);
    }
}
