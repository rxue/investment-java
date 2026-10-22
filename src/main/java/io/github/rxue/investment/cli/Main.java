package io.github.rxue.investment.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "investment", description = "Investment command line tool", mixinStandardHelpOptions = true)
public class Main implements Runnable {
    @Spec
    private CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main())
                .addSubcommand(new QuotesCommand())
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // no subcommand given
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
