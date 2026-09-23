package com.example.ledger.domain.usecase

import com.example.ledger.data.local.dao.SmartRuleDao
import com.example.ledger.data.local.entities.SmartRuleEntity
import com.example.ledger.domain.model.MatchField
import javax.inject.Inject

data class RuleMatch(val rule: SmartRuleEntity)

/**
 * Evaluates enabled smart rules, highest priority first, against a message's sender and
 * body. Returns every match (not just the first) so the caller can flag "matched multiple
 * rules" transactions for the review queue, per README's smart-rules review criteria —
 * but applies only the highest-priority match's category/type/source automatically.
 */
class ApplySmartRulesUseCase @Inject constructor(
    private val smartRuleDao: SmartRuleDao,
) {
    suspend operator fun invoke(sender: String, body: String): List<RuleMatch> {
        val rules = smartRuleDao.getEnabledOrderedByPriority()
        return rules.filter { rule -> matches(rule, sender, body) }.map(::RuleMatch)
    }

    private fun matches(rule: SmartRuleEntity, sender: String, body: String): Boolean {
        val haystack = when (MatchField.valueOf(rule.matchField)) {
            MatchField.SENDER -> sender
            MatchField.MESSAGE_TEXT -> body
            MatchField.BOTH -> "$sender $body"
        }
        return if (rule.isRegex) {
            val options = if (rule.caseInsensitive) setOf(RegexOption.IGNORE_CASE) else emptySet()
            runCatching { Regex(rule.pattern, options).containsMatchIn(haystack) }.getOrDefault(false)
        } else {
            haystack.contains(rule.pattern, ignoreCase = rule.caseInsensitive)
        }
    }
}
