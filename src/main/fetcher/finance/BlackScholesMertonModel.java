package main.fetcher.finance;

// https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model
public class BlackScholesMertonModel {
    // Call option
    // C = S * exp(-k*t) * N(d1) - exp(-r * T) * L * N(d2)
    // d1 = (ln(S / L) + (r - k + 0.5 * sigma^2) * T) / (sigma * sqrt(T))
    // d2 = d1 - sigma * sqrt(T)
    //
    // k : year dividend, k = 0 for no dividend options
    // ln : natural logarithm base = e = 2.718281828
    // C : Option original rational price
    // L : Option strike price
    // S : Exchange Stock Asset current price
    // T : Option valid period
    // r : continuous compound risk-free rate H
    // sigma^2 : annual variance
    // N() : Cumulative probability function of Normal distribution
}
